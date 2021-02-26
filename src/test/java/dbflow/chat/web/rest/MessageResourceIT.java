package dbflow.chat.web.rest;

import dbflow.chat.Dbflowchat2App;
import dbflow.chat.config.TestSecurityConfiguration;
import dbflow.chat.domain.Message;
import dbflow.chat.repository.MessageRepository;
import dbflow.chat.repository.search.MessageSearchRepository;
import dbflow.chat.service.MessageService;
import dbflow.chat.service.dto.MessageDTO;
import dbflow.chat.service.mapper.MessageMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link MessageResource} REST controller.
 */
@SpringBootTest(classes = { Dbflowchat2App.class, TestSecurityConfiguration.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MessageResourceIT {

    private static final String DEFAULT_FROM = "AAAAAAAAAA";
    private static final String UPDATED_FROM = "BBBBBBBBBB";

    private static final String DEFAULT_TO = "AAAAAAAAAA";
    private static final String UPDATED_TO = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Long DEFAULT_ORDER = 1L;
    private static final Long UPDATED_ORDER = 2L;

    private static final LocalDate DEFAULT_EMITTED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EMITTED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RECEIVED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RECEIVED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_READ_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_READ_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUD = 1D;
    private static final Double UPDATED_LONGITUD = 2D;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageService messageService;

    /**
     * This repository is mocked in the dbflow.chat.repository.search test package.
     *
     * @see dbflow.chat.repository.search.MessageSearchRepositoryMockConfiguration
     */
    @Autowired
    private MessageSearchRepository mockMessageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageMockMvc;

    private Message message;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .from(DEFAULT_FROM)
            .to(DEFAULT_TO)
            .body(DEFAULT_BODY)
            .order(DEFAULT_ORDER)
            .emittedDate(DEFAULT_EMITTED_DATE)
            .receivedDate(DEFAULT_RECEIVED_DATE)
            .readDate(DEFAULT_READ_DATE)
            .latitude(DEFAULT_LATITUDE)
            .longitud(DEFAULT_LONGITUD);
        return message;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager em) {
        Message message = new Message()
            .from(UPDATED_FROM)
            .to(UPDATED_TO)
            .body(UPDATED_BODY)
            .order(UPDATED_ORDER)
            .emittedDate(UPDATED_EMITTED_DATE)
            .receivedDate(UPDATED_RECEIVED_DATE)
            .readDate(UPDATED_READ_DATE)
            .latitude(UPDATED_LATITUDE)
            .longitud(UPDATED_LONGITUD);
        return message;
    }

    @BeforeEach
    public void initTest() {
        message = createEntity(em);
    }

    @Test
    @Transactional
    public void createMessage() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();
        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        restMessageMockMvc.perform(post("/api/messages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isCreated());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getFrom()).isEqualTo(DEFAULT_FROM);
        assertThat(testMessage.getTo()).isEqualTo(DEFAULT_TO);
        assertThat(testMessage.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testMessage.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testMessage.getEmittedDate()).isEqualTo(DEFAULT_EMITTED_DATE);
        assertThat(testMessage.getReceivedDate()).isEqualTo(DEFAULT_RECEIVED_DATE);
        assertThat(testMessage.getReadDate()).isEqualTo(DEFAULT_READ_DATE);
        assertThat(testMessage.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testMessage.getLongitud()).isEqualTo(DEFAULT_LONGITUD);

        // Validate the Message in Elasticsearch
        verify(mockMessageSearchRepository, times(1)).save(testMessage);
    }

    @Test
    @Transactional
    public void createMessageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = messageRepository.findAll().size();

        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc.perform(post("/api/messages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeCreate);

        // Validate the Message in Elasticsearch
        verify(mockMessageSearchRepository, times(0)).save(message);
    }


    @Test
    @Transactional
    public void getAllMessages() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc.perform(get("/api/messages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM)))
            .andExpect(jsonPath("$.[*].to").value(hasItem(DEFAULT_TO)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER.intValue())))
            .andExpect(jsonPath("$.[*].emittedDate").value(hasItem(DEFAULT_EMITTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].receivedDate").value(hasItem(DEFAULT_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].readDate").value(hasItem(DEFAULT_READ_DATE.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitud").value(hasItem(DEFAULT_LONGITUD.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.from").value(DEFAULT_FROM))
            .andExpect(jsonPath("$.to").value(DEFAULT_TO))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER.intValue()))
            .andExpect(jsonPath("$.emittedDate").value(DEFAULT_EMITTED_DATE.toString()))
            .andExpect(jsonPath("$.receivedDate").value(DEFAULT_RECEIVED_DATE.toString()))
            .andExpect(jsonPath("$.readDate").value(DEFAULT_READ_DATE.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitud").value(DEFAULT_LONGITUD.doubleValue()));
    }
    @Test
    @Transactional
    public void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get("/api/messages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).get();
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage
            .from(UPDATED_FROM)
            .to(UPDATED_TO)
            .body(UPDATED_BODY)
            .order(UPDATED_ORDER)
            .emittedDate(UPDATED_EMITTED_DATE)
            .receivedDate(UPDATED_RECEIVED_DATE)
            .readDate(UPDATED_READ_DATE)
            .latitude(UPDATED_LATITUDE)
            .longitud(UPDATED_LONGITUD);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        restMessageMockMvc.perform(put("/api/messages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isOk());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
        Message testMessage = messageList.get(messageList.size() - 1);
        assertThat(testMessage.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testMessage.getTo()).isEqualTo(UPDATED_TO);
        assertThat(testMessage.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testMessage.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testMessage.getEmittedDate()).isEqualTo(UPDATED_EMITTED_DATE);
        assertThat(testMessage.getReceivedDate()).isEqualTo(UPDATED_RECEIVED_DATE);
        assertThat(testMessage.getReadDate()).isEqualTo(UPDATED_READ_DATE);
        assertThat(testMessage.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testMessage.getLongitud()).isEqualTo(UPDATED_LONGITUD);

        // Validate the Message in Elasticsearch
        verify(mockMessageSearchRepository, times(1)).save(testMessage);
    }

    @Test
    @Transactional
    public void updateNonExistingMessage() throws Exception {
        int databaseSizeBeforeUpdate = messageRepository.findAll().size();

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc.perform(put("/api/messages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Message in Elasticsearch
        verify(mockMessageSearchRepository, times(0)).save(message);
    }

    @Test
    @Transactional
    public void deleteMessage() throws Exception {
        // Initialize the database
        messageRepository.saveAndFlush(message);

        int databaseSizeBeforeDelete = messageRepository.findAll().size();

        // Delete the message
        restMessageMockMvc.perform(delete("/api/messages/{id}", message.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Message> messageList = messageRepository.findAll();
        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Message in Elasticsearch
        verify(mockMessageSearchRepository, times(1)).deleteById(message.getId());
    }

    @Test
    @Transactional
    public void searchMessage() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        messageRepository.saveAndFlush(message);
        when(mockMessageSearchRepository.search(queryStringQuery("id:" + message.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(message), PageRequest.of(0, 1), 1));

        // Search the message
        restMessageMockMvc.perform(get("/api/_search/messages?query=id:" + message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM)))
            .andExpect(jsonPath("$.[*].to").value(hasItem(DEFAULT_TO)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER.intValue())))
            .andExpect(jsonPath("$.[*].emittedDate").value(hasItem(DEFAULT_EMITTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].receivedDate").value(hasItem(DEFAULT_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].readDate").value(hasItem(DEFAULT_READ_DATE.toString())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitud").value(hasItem(DEFAULT_LONGITUD.doubleValue())));
    }
}
