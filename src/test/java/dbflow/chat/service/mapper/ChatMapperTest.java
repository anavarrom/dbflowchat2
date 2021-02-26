package dbflow.chat.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ChatMapperTest {

    private ChatMapper chatMapper;

    @BeforeEach
    public void setUp() {
        chatMapper = new ChatMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(chatMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(chatMapper.fromId(null)).isNull();
    }
}
