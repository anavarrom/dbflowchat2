package dbflow.chat.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import dbflow.chat.web.rest.TestUtil;

public class ChatDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatDTO.class);
        ChatDTO chatDTO1 = new ChatDTO();
        chatDTO1.setId(1L);
        ChatDTO chatDTO2 = new ChatDTO();
        assertThat(chatDTO1).isNotEqualTo(chatDTO2);
        chatDTO2.setId(chatDTO1.getId());
        assertThat(chatDTO1).isEqualTo(chatDTO2);
        chatDTO2.setId(2L);
        assertThat(chatDTO1).isNotEqualTo(chatDTO2);
        chatDTO1.setId(null);
        assertThat(chatDTO1).isNotEqualTo(chatDTO2);
    }
}
