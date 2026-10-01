package ee.liftertrans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Sama DTO-sse loeme ka AI mudeli JSON vastuse {"answer": "..."}, seega on vaja tühja konstruktorit ja settereid
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAskResponseDto {

    private String answer;
}
