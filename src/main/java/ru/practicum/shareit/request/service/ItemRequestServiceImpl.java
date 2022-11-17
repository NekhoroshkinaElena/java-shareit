package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoOutput add(ItemRequestDtoInput itemRequestDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь c идентификатором " + userId + " не найден.");
            return new NotFoundException("Пользователь c идентификатором " + userId + " не найден.");
        }));
        itemRequest.setCreated(LocalDateTime.now());
        requestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDtoOutput(itemRequest);
    }

    @Override
    public List<ItemRequestDtoOutput> findAllOwner(long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.error("пользователь не найден");
            throw new NotFoundException("пользователь не найден");
        });
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoOutput)
                .peek(itemRequestDtoOutput ->
                        itemRequestDtoOutput.setItems(
                                itemRepository.findAllByRequestId(itemRequestDtoOutput.getId())
                                        .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOutput> findAll(int from, int size, long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.error("пользователь не найден");
            throw new NotFoundException("пользователь не найден");
        });
        if (from < 0 || size < 0) {
            log.error("параметры не могут быть отрицательными");
            throw new ValidationException("параметры не могут быть отрицательными");
        }
        if (size == 0 && from == 0) {
            throw new ValidationException("параметры не могут быть пустыми");
        }

        return requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId,
                        PageRequest.of(from / size, size)).stream().map(ItemRequestMapper::toItemRequestDtoOutput)
                .peek(itemRequestDtoOutput ->
                        itemRequestDtoOutput.setItems(
                                itemRepository.findAllByRequestId(itemRequestDtoOutput.getId()).stream()
                                        .map(ItemMapper::toItemDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOutput findById(long requestId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.error("пользователь не найден");
            throw new NotFoundException("пользователь не найден");
        });
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("запрос с id " + requestId + " не существует");
            throw new NotFoundException("запрос с id " + requestId + " не существует");
        });
        ItemRequestDtoOutput itemRequestDtoOutput = ItemRequestMapper.toItemRequestDtoOutput(itemRequest);
        itemRequestDtoOutput.setItems(itemRepository
                .findAllByRequestId(requestId)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return itemRequestDtoOutput;
    }
}
