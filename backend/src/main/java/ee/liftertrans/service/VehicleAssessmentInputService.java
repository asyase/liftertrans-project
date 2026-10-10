package ee.liftertrans.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ee.liftertrans.ai.VehicleAssessmentInputDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

@Service

//loeb kliendi küsimusest välja kaalu ja mõõdud
public class VehicleAssessmentInputService {

    //lisame service-le AI-kliendi, millega ta saab AI-ga suhelda
    private final ChatClient chatClient;
    //service saab promti Markdown-failistb lugeda
    private final ResourceLoader resourceLoader;


//Konstruktor on terve see osa. ResourceLoader aitab promtifaili avada ja ChatClient aitab AI-le küsimuse saata
    public VehicleAssessmentInputService(ChatClient.Builder builder, ResourceLoader resourceLoader) {
        this.chatClient = builder.build();
        this.resourceLoader = resourceLoader;
    }

    //abimeetod, mis aitab promptfaili lugeda
    private String loadSystemPrompt() {
        try {


        //ütleme Springile, kust promptfaili leida
        Resource promptResource = resourceLoader.getResource("classpath:prompts/vehicle-assessment-system-prompt.md");
        //anname AI-le teksti, mitte faili viidet
        String promptText = promptResource.getContentAsString(StandardCharsets.UTF_8);
        //promtis ainult see osa, mida AI tohib lugeda, osa algus
        int start = promptText.indexOf("<!-- PROMPTI_SISU_ALGUS -->");
        //osa lõpp
        int end = promptText.indexOf("<!-- PROMPTI_SISU_LOPP -->");
        //Kui märgendit failist ei leita, tagastab indexOf väärtuse -1; enne teksti lõikamist kontrollime, et mõlemad leiti.
        if (start == -1 || end == -1) {
            throw new IllegalStateException("Prompti märgendeid ei leitud");

        }

        //võtame tekstist ainult selle osa, mida AI tohib näha
        int contentStart = start + "<!-- PROMPTI_SISU_ALGUS -->".length();


        //võtab algusmärgendi ja lõppmärgendi vahelt teksti ning eemaldab äärest liigsed tühikud.
        return promptText.substring(contentStart, end).trim();


    }

        catch (IOException e) {
            throw new IllegalStateException("Promptifaili lugemine ebaõnnestus", e);
        }}

    //meetod, mis saab kliendi küsimus tekstina sisse ja tagastab sellest eraldatud andmed
public VehicleAssessmentInputDto extractAssessmentInput(String question) {

        //meetod kutsub chatClienti, annab talle süsteemijuhise ja kasutaja teksti ning palub vastuse teha Dtoks
        VehicleAssessmentInputDto vehicleAssessmentInputDto = chatClient.prompt()
                .system(loadSystemPrompt())
                .user(question)
                .call()
                .entity(VehicleAssessmentInputDto.class);

        //kontrollime, et kas AI andis vastuse.
        if (vehicleAssessmentInputDto == null) {
            throw new IllegalStateException("AI mudel tagastas tühja vastuse");
        }

        if (vehicleAssessmentInputDto.getMissingInformation() == null) {
            throw new IllegalStateException("AI mudeli vastusest puudub missingInformation");
        }

        //meetod peab selle Dto ka tagastama
        return vehicleAssessmentInputDto;




}
}
