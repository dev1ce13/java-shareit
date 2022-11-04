package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.user.exception.IllegalUserException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto add(long userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = ItemMapper.mapToItem(itemDto, userId);
        return ItemMapper.mapToItemDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        Item oldItem = getItemById(itemId);
        if (userId == oldItem.getOwnerId()) {
            Item item = ItemMapper.mapToItem(itemDto, userId);
            item.setId(itemId);
            Item newItem = buildingItem(item, oldItem);
            return ItemMapper.mapToItemDto(repository.save(newItem));
        } else {
            throw new IllegalUserException(
                    String.format("User with ID=%s does not have access to item with ID=%s", userId, itemId)
            );
        }
    }

    @Override
    @Transactional
    public ItemsByOwnerDto getById(long itemId, long userId) {
        ItemsByOwnerDto item = ItemMapper.mapToItemsByOwnerDto(getItemById(itemId));
        if (item.getOwnerId() == userId) {
            item.setLastBooking(BookingMapper.mapToBookingDto(bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(
                    item.getId(),
                    LocalDateTime.now(),
                    BookingStatus.REJECTED)));
            item.setNextBooking(BookingMapper.mapToBookingDto(bookingRepository.findByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                    item.getId(),
                    BookingStatus.APPROVED,
                    LocalDateTime.now())));
        }
        item.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toSet()));
        System.out.println(commentRepository.findAllByItemId(itemId));
        return item;
    }

    @Override
    @Transactional
    public List<ItemsByOwnerDto> getItemsByUserId(long userId, int from, Integer size) {
        userService.getById(userId);
        List<Item> items = repository.findAllByOwnerId(userId);
        checkingFromParameter(from, items.size());
        if (size != null) {
            return items.subList(from, items.size())
                    .stream()
                    .limit(size)
                    .map(ItemMapper::mapToItemsByOwnerDto)
                    .peek(i -> i.setLastBooking(BookingMapper.mapToBookingDto(
                                    bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(
                                            i.getId(),
                                            LocalDateTime.now(),
                                            BookingStatus.REJECTED)
                            ))
                    )
                    .peek(i -> i.setNextBooking(BookingMapper.mapToBookingDto(
                                    bookingRepository.findByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                                            i.getId(),
                                            BookingStatus.APPROVED,
                                            LocalDateTime.now())
                            ))
                    )
                    .peek(i -> {
                        i.setComments(commentRepository.findAllByItemId(i.getId()).stream()
                                .map(CommentMapper::mapToCommentDto)
                                .collect(Collectors.toSet()));
                    })
                    .sorted((i1, i2) -> (int) (i1.getId() - i2.getId()))
                    .collect(Collectors.toList());
        } else {
            return items.subList(from, items.size())
                    .stream()
                    .map(ItemMapper::mapToItemsByOwnerDto)
                    .peek(i -> i.setLastBooking(BookingMapper.mapToBookingDto(
                                    bookingRepository.findByItem_IdAndEndBeforeAndStatusNotOrderByEndDesc(
                                            i.getId(),
                                            LocalDateTime.now(),
                                            BookingStatus.REJECTED)
                            ))
                    )
                    .peek(i -> i.setNextBooking(BookingMapper.mapToBookingDto(
                                    bookingRepository.findByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                                            i.getId(),
                                            BookingStatus.APPROVED,
                                            LocalDateTime.now())
                            ))
                    )
                    .peek(i -> {
                        i.setComments(commentRepository.findAllByItemId(i.getId()).stream()
                                .map(CommentMapper::mapToCommentDto)
                                .collect(Collectors.toSet()));
                    })
                    .sorted((i1, i2) -> (int) (i1.getId() - i2.getId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public List<ItemDto> getSearch(String text, int from, Integer size) {
        if (text.isEmpty()) {
            return List.of();
        }
        List<Item> items = repository.findAllByNameOrDescriptionContainingIgnoreCase(text, text);
        checkingFromParameter(from, items.size());
        if (size != null) {
            return items.subList(from, items.size())
                    .stream()
                    .filter(Item::getAvailable)
                    .limit(size)
                    .map(ItemMapper::mapToItemDto)
                    .collect(Collectors.toList());
        } else {
            return items.subList(from, items.size())
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::mapToItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public Item getItemById(long id) {
        Optional<Item> item = repository.findById(id);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new ItemNotFoundException(String.format("Item with ID=%s doesn't exist", id));
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        Comment comment = CommentMapper.mapToComment(
                commentDto,
                UserMapper.mapToUser(userService.getById(userId)),
                itemId);
        Booking booking = bookingRepository.findByBookerIdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (booking != null) {
            return CommentMapper.mapToCommentDto(commentRepository.save(comment));
        } else {
            throw new IllegalArgumentException(
                    String.format("User with ID=%s does not use item with ID=%s",
                            userId,
                            itemId));
        }
    }

    private Item buildingItem(Item newItem, Item oldItem) {
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        if (newItem.getRequestId() == null) {
            newItem.setRequestId(oldItem.getRequestId());
        }
        return newItem;
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }
}
