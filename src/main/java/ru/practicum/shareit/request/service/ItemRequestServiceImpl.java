package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto,
                UserMapper.mapToUser(userService.getById(userId)));
        return ItemRequestMapper.mapToItemRequestDto(repository.save(itemRequest));
    }

    @Override
    @Transactional
    public List<ItemRequestWithAnswerDto> getUserRequests(long userId) {
        userService.getById(userId);
        List<ItemRequest> requests = repository.findAllByRequester_IdOrderByCreatedDesc(userId);
        List<ItemRequestWithAnswerDto> result = new ArrayList<>();
        requests.forEach(itemRequest -> {
            List<ItemByRequestDto> items = itemRepository.findAllByRequestId(itemRequest.getId())
                    .stream()
                    .map(ItemMapper::mapToItemByRequestDto)
                    .collect(Collectors.toList());
            result.add(ItemRequestMapper.mapToItemRequestWithAnswerDto(itemRequest, items));
        });
        return result;
    }

    @Override
    @Transactional
    public List<ItemRequestWithAnswerDto> getRequests(long userId, int from, Integer size) {
        List<ItemRequest> requests = repository.findAllByRequester_IdNotOrderByCreatedDesc(userId);
        checkingFromParameter(from, requests.size());
        List<ItemRequestWithAnswerDto> result = new ArrayList<>();
        requests.forEach(itemRequest -> {
            List<ItemByRequestDto> items = itemRepository.findAllByRequestId(itemRequest.getId())
                    .stream()
                    .map(ItemMapper::mapToItemByRequestDto)
                    .collect(Collectors.toList());
            result.add(ItemRequestMapper.mapToItemRequestWithAnswerDto(itemRequest, items));
        });
        if (size != null) {
            return result.subList(from, requests.size())
                    .stream()
                    .limit(size)
                    .collect(Collectors.toList());
        } else {
            return result.subList(from, requests.size());
        }
    }

    @Override
    @Transactional
    public ItemRequestWithAnswerDto getRequestById(long requestId, long userId) {
        userService.getById(userId);
        Optional<ItemRequest> itemRequest = repository.findById(requestId);
        if (itemRequest.isPresent()) {
            List<ItemByRequestDto> items = itemRepository.findAllByRequestId(requestId)
                    .stream()
                    .map(ItemMapper::mapToItemByRequestDto)
                    .collect(Collectors.toList());
            return ItemRequestMapper.mapToItemRequestWithAnswerDto(itemRequest.get(), items);
        } else {
            throw new ItemRequestNotFoundException(String.format("Request with ID=%s not found", requestId));
        }
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }
}
