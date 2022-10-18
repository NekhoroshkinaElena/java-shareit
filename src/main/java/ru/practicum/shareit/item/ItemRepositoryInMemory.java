package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
@Setter
@RequiredArgsConstructor
public class ItemRepositoryInMemory {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    public long getIdItem() {
        return id++;
    }

    public Item create(Item item) {
        item.setId(getIdItem());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item update(Item item, long id) {
        Item itemUpdate = items.get(id);
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        }
        items.put(id, itemUpdate);
        return itemUpdate;
    }

    public Item getById(long id) {
        return items.get(id);
    }

    public List<Item> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        String textR = text.toLowerCase();
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().trim().toLowerCase().contains(textR) ||
                    item.getName().trim().toLowerCase().contains(textR)) {
                if (item.getAvailable()) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
