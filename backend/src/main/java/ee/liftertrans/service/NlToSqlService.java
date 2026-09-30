package ee.liftertrans.service;

import ee.liftertrans.dto.AiAskResponseDto;
import ee.liftertrans.infrastructure.exception.IncorrectInputException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NlToSqlService {

    // Mudel näeb ainult neid tabeleid. Tabelit "user" (paroolide räsid) ei anna me mudelile üldse teada
    private static final String SQL_SYSTEM_PROMPT = """
        You are a PostgreSQL query generator for the Liftertrans database.

        SCHEMA:
        liftertrans_project.customer(
            id INTEGER PRIMARY KEY,
            name VARCHAR(150) NOT NULL,
            company_name VARCHAR(150),
            email VARCHAR(150),
            phone VARCHAR(30) NOT NULL,
            created_at TIMESTAMP,
            company_registration_number VARCHAR(20),
            vat_number VARCHAR(30),
            invoice_email VARCHAR(150)
        )

        liftertrans_project.driver(
            id INTEGER PRIMARY KEY,
            name VARCHAR(150) NOT NULL,
            phone VARCHAR(30) NOT NULL,
            email VARCHAR(150),
            active BOOLEAN NOT NULL
        )

        liftertrans_project.job(
            id INTEGER PRIMARY KEY,
            customer_id INTEGER NOT NULL REFERENCES liftertrans_project.customer(id),
            driver_id INTEGER REFERENCES liftertrans_project.driver(id),
            vehicle_id INTEGER,
            subcontractor_id INTEGER,
            job_type VARCHAR(30) NOT NULL, -- 'TRANSPORT_AND_CRANE' or 'CRANE_ONLY'
            execution_type VARCHAR(20) NOT NULL, -- 'INTERNAL' or 'SUBCONTRACTED'
            pickup_address VARCHAR(255),
            delivery_address VARCHAR(255),
            service_address VARCHAR(255),
            receiver_name VARCHAR(150),
            receiver_phone VARCHAR(30),
            planned_start_time TIMESTAMP NOT NULL,
            planned_end_time TIMESTAMP,
            actual_start_time TIMESTAMP,
            actual_finish_time TIMESTAMP,
            estimated_km DECIMAL(10,2),
            actual_km DECIMAL(10,2),
            estimated_hours DECIMAL(6,2),
            actual_hours DECIMAL(6,2),
            status VARCHAR(20) NOT NULL, -- 'DRAFT', 'PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
            notes TEXT,
            created_at TIMESTAMP,
            updated_at TIMESTAMP
        )

        RULES:
        1. Return JSON {"answer": "<sql>"} where <sql> is ONLY one raw SQL SELECT statement.
        2. Do not return markdown or explanations.
        3. Use only the tables and columns defined above, always with the liftertrans_project. prefix.
        4. Never generate INSERT, UPDATE, DELETE, DROP, ALTER or TRUNCATE.
        5. If the question cannot be answered using this schema, return {"answer": "CANNOT_ANSWER"}.
        6. Use PostgreSQL syntax.
        """;

    private static final String SQL_USER_PROMPT_TEMPLATE = """
            Generate SQL syntax for the question below

            %s
            """;

    private static final String SUMMARY_SYSTEM_PROMPT = """
            You are a helpful assistant that summarizes data clearly.
            Answer in the same language as the question.
            Return JSON {"answer": "<summary>"}.
            """;

    private static final String SUMMARY_USER_PROMPT_TEMPLATE = """
            Summarize this database query result in 1-3 plain sentences.
            Don't mention SQL or technical terms. Be specific about numbers.

            Question: %s
            Results (%d rows): %s
            """;

    private static final String CANNOT_ANSWER = "CANNOT_ANSWER";

    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;

    public NlToSqlService(JdbcTemplate jdbcTemplate, ChatClient.Builder builder) {
        this.jdbcTemplate = jdbcTemplate;
        this.chatClient = builder.build();
    }

    public AiAskResponseDto ask(String userQuestion) {
        String generatedSql = generateSql(userQuestion);
        List<Map<String, Object>> databaseResults = jdbcTemplate.queryForList(generatedSql);

        return generateResponse(userQuestion, databaseResults);
    }

    private String generateSql(String userQuestion) {
        String generatedSql = callLlm(SQL_SYSTEM_PROMPT, SQL_USER_PROMPT_TEMPLATE.formatted(userQuestion)).getAnswer().trim();
        validateSqlQuery(generatedSql);

        return generatedSql;
    }

    private void validateSqlQuery(String generatedSql) {
        if (generatedSql.equals(CANNOT_ANSWER)) {
            throw new IncorrectInputException("Sellele küsimusele ei saa andmebaasi põhjal vastata", "CANNOT_ANSWER");
        }

        String upperCaseSql = generatedSql.toUpperCase();

        if (!upperCaseSql.startsWith("SELECT")) {
            throw new IncorrectInputException("Lubatud on ainult SELECT päringud", "FORBIDDEN_SQL");
        }

        if (upperCaseSql.matches("(?s).*\\b(DROP|DELETE|INSERT|UPDATE|TRUNCATE|ALTER|GRANT|COPY|CALL|DO)\\b.*")) {
            throw new IncorrectInputException("Päring sisaldab keelatud SQL märksõnu", "FORBIDDEN_SQL");
        }

        // Kasutajate tabelis on paroolide räsid — seda ei tohi välja anda
        if (upperCaseSql.matches("(?s).*\\b(\"USER\"|USER|PASSWORD_HASH)\\b.*")) {
            throw new IncorrectInputException("Kasutajate andmeid ei saa küsida", "FORBIDDEN_SQL");
        }
    }

    private AiAskResponseDto generateResponse(String userQuestion, List<Map<String, Object>> databaseResults) {
        String userPrompt = SUMMARY_USER_PROMPT_TEMPLATE.formatted(
                userQuestion,
                databaseResults.size(),
                databaseResults);

        return callLlm(SUMMARY_SYSTEM_PROMPT, userPrompt);
    }

    private AiAskResponseDto callLlm(String systemPrompt, String userPrompt) {
        AiAskResponseDto aiAskResponseDto = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(AiAskResponseDto.class);

        if (aiAskResponseDto == null || aiAskResponseDto.getAnswer() == null) {
            throw new IllegalStateException("AI mudel tagastas tühja vastuse");
        }

        return aiAskResponseDto;
    }
}
