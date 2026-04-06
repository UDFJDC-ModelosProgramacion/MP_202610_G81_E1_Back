package co.edu.udistrital.mdp.pets.controllers;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.udistrital.mdp.pets.dto.MessageDTO;
import co.edu.udistrital.mdp.pets.dto.MessageDetailDTO;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MessageService;
@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDetailDTO createMessage(@RequestBody MessageDetailDTO dto)
            throws IllegalOperationException {
        MessageEntity entity = modelMapper.map(dto, MessageEntity.class);
        return modelMapper.map(messageService.createMessage(entity), MessageDetailDTO.class);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> getMessages() {
        List<MessageEntity> entities = messageService.getMessages();
        return modelMapper.map(entities, new TypeToken<List<MessageDTO>>() {}.getType());
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDetailDTO getMessage(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(messageService.getMessage(id), MessageDetailDTO.class);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDetailDTO updateMessage(@PathVariable Long id, @RequestBody MessageDetailDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        MessageEntity entity = modelMapper.map(dto, MessageEntity.class);
        return modelMapper.map(messageService.updateMessage(id, entity), MessageDetailDTO.class);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long id,
            @RequestParam Long requestingUserId,
            @RequestParam boolean isAdopter)
            throws EntityNotFoundException, IllegalOperationException {
        messageService.deleteMessage(id, requestingUserId, isAdopter);
    }
    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public MessageDetailDTO markAsRead(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(messageService.markAsRead(id), MessageDetailDTO.class);
    }
}
