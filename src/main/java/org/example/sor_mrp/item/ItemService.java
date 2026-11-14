package org.example.sor_mrp.item;

import java.util.Map;
import java.util.Optional;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  private final ItemRepository itemRepository;

  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public Item createItem(CreateItemRequestDto request) {
    // 1. Check if an item with the same item number already exists
    if (itemRepository.findByItemNumber(request.getItemNumber()).isPresent()) {
      throw new IllegalStateException("Item number already exists: " + request.getItemNumber());
    }

    // 2. Map DTO to Domain Model
    Item item = new Item();
    item.setItemNumber(request.getItemNumber());
    item.setItemType(request.getItemType() != null ? request.getItemType() : ItemType.BASE);
    item.setQuantity(request.getQuantity());
    item.setDescription(request.getDescription());

    // 3. Save via repository
    return itemRepository.save(item);
  }

  public Optional<Item> getItemById(Long id) {
    return itemRepository.findById(id);
  }

  public Optional<Item> getItemByItemNumber(String itemNumber) {
    return itemRepository.findByItemNumber(itemNumber);
  }

  public Item updateItemById(Long id, UpdateItemRequestDto request) {
    // 1. Ensure the item exists before updating
    Item existingItem =
        itemRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));

    // 2. If the item number is being changed, check that the new number isn't already taken
    if (!existingItem.getItemNumber().equals(request.getItemNumber())) {
      if (itemRepository.findByItemNumber(request.getItemNumber()).isPresent()) {
        throw new IllegalStateException("Item number already exists: " + request.getItemNumber());
      }
    }

    // 3. Update fields from the request DTO
    existingItem.setItemNumber(request.getItemNumber());
    existingItem.setItemType(request.getItemType());
    existingItem.setQuantity(request.getQuantity());
    existingItem.setDescription(request.getDescription());

    // FIXED: Use itemRepository.save() for Hibernate updates since the entity has an ID
    return itemRepository.save(existingItem);
  }

  public void deleteItemById(Long id) {
    if (itemRepository.findById(id).isEmpty()) {
      throw new IllegalArgumentException("Item not found with id: " + id);
    }
    itemRepository.deleteById(id);
  }

  public JdbcPaginationHelper.PaginatedResult<Item> queryItems(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    // Adjust for 0-indexed pages if client sends 1-indexed (e.g., page=1 becomes 0)
    int pageIndex = Math.max(0, page - 1);
    return itemRepository.findAllFiltered(filters, search, pageIndex, size, sortBy, sortDir);
  }
}
