package dbflow.chat.service.mapper;


import dbflow.chat.domain.*;
import dbflow.chat.service.dto.ChatDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Chat} and its DTO {@link ChatDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ChatMapper extends EntityMapper<ChatDTO, Chat> {


    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "removeMessage", ignore = true)
    Chat toEntity(ChatDTO chatDTO);

    default Chat fromId(Long id) {
        if (id == null) {
            return null;
        }
        Chat chat = new Chat();
        chat.setId(id);
        return chat;
    }
}
