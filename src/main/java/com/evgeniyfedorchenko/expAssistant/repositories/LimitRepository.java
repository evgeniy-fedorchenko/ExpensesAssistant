package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей, содержащей объекты Limit - денежные лимиты, устанавливаемые для новых транзакций
 */
@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    /**
     * Метод для получения последнего зарегистрированного объекта Limit
     * @return Optional.empty(), если хотя не было сохранено ни одного объекта Limit, Optional.of(Limit) в ином случае
     */
    @Query(value = "SELECT * FROM limits WHERE id = (SELECT MAX(id) FROM limits)", nativeQuery = true)
    Optional<Limit> findByMaxId();
}
