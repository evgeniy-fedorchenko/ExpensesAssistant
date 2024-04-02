<a1>Expenses assistent</a1>

Это приложение помогает следить за вашими тратами в нескольких валютах. Просто регистрируйте совершенные операции, устанавливайте кастомные лимиты для отдельных категорий совершаемых платежей и вы всегда будете помнить о своих расходах и не позволять им расрастись слишком сильно, ведь всегда можете получить актуальную информацию о чрезмерных тратах, включая суммы, даты их совершения, категорю и валюту.
Для трансвалютных операций всегда используется актуальная информация последнего дня закрытия биржевых торгов выбранной валюты

Приложение представляет собой микросервис, состоящий из двух API - одного клиента для обращения к внешнему API, и использующего базу данных для хранения бизнес-сущностей и курсов валют.
Этот сервис: 
1. Получает информацию о каждой расходной операции в тенге (KZT), рублях (RUB) и других валютах в реальном времени и сохраняет ее в своей собственной базе данных.
2. Хранит месячный лимит по расходам в долларах США (USD) раздельно для двух категорий расходов: товаров и услуг. Если лимит не установлен, принимается равным 1000 USD.
3. Запрашивает данные биржевых курсов валютных пар KZT/USD, RUB/USD по дневному интервалу (1day/daily) и хранит их в собственной базе данных. При расчете курсов использовать данные закрытия (close).
   В случае, если таковые недоступны на текущий день (выходной или праздничный день), то используются данные последнего закрытия (previous_close)
5. Помечает транзакции, превысившие месячный лимит операций (технический флаг limit_exceeded)
6. Дает возможность клиенту установить новый лимит. При установлении нового лимита микросервисом автоматически выставляется текущая дата, не позволяя выставить ее в прошедшем или будущем времени. Не позволяет обновлять существующие лимиты
7. По запросу клиента возвращается список транзакций, превысивших лимит, с указанием лимита, который был превышен (дата установления, сумма лимита, валюта (USD)).


Используемые инструменты и технологии:
     - Java:
• BigDecimal, Date types, enums
• Collections, equals and hashCode, Stream API, Lambdas
• Обработка исключений
• Code conventions
• Multithreading
     - Фреймворки, библиотеки и Build Tools:
• Spring WEB, бины, компоненты, конфигурации
• ORM (Spring Data JPA, Hibernate): Entities, Transactions, Relations (ManyToOne, OneToMany), native queries, data mappings
• DTO-Entity mappers
• Конфигурации Spring Boot автоконфигурация с исключениями*
• Maven (Bill of Materials, dependency management, build plugins)
     - БД:
• Язык SQL, нормальные формы, отношения таблиц, внешние ключи, подзапросы
• Инструмент миграции Liquibase
     - Docker
* Dockerfile и docker-compose