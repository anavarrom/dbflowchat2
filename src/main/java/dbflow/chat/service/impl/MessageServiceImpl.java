package dbflow.chat.service.impl;

import dbflow.chat.service.MessageService;
import dbflow.chat.domain.Message;
import dbflow.chat.repository.MessageRepository;
import dbflow.chat.repository.search.MessageSearchRepository;
import dbflow.chat.service.dto.MessageDTO;
import dbflow.chat.service.mapper.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Message}.
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    private final MessageSearchRepository messageSearchRepository;

    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, MessageSearchRepository messageSearchRepository) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageSearchRepository = messageSearchRepository;
    }

    @Override
    public MessageDTO save(MessageDTO messageDTO) {
        log.debug("Request to save Message : {}", messageDTO);
        Message message = messageMapper.toEntity(messageDTO);
        message = messageRepository.save(message);
        MessageDTO result = messageMapper.toDto(message);
        messageSearchRepository.save(message);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Messages");
        return messageRepository.findAll(pageable)
            .map(messageMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<MessageDTO> findOne(Long id) {
        log.debug("Request to get Message : {}", id);
        return messageRepository.findById(id)
            .map(messageMapper::toDto);
    }

	@Override
	public Page<MessageDTO> findAllByChat(Long chatId, Pageable pageable) {
		// TODO Auto-generated method stub
        return messageRepository.findAllByChatId(chatId, pageable).map(messageMapper::toDto);
	}
	@Override
    public void delete(Long id) {
        log.debug("Request to delete Message : {}", id);
        messageRepository.deleteById(id);
        messageSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Messages for query {}", query);
        return messageSearchRepository.search(queryStringQuery(query), pageable)
            .map(messageMapper::toDto);
    }

}
