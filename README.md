# Expenses assistent :dollar:

Это приложение помогает следить за вашими тратами в нескольких валютах. Просто регистрируйте совершенные операции, устанавливайте кастомные лимиты для отдельных категорий платежей и вы всегда будете помнить о своих расходах, не позволяя им выйти из-под контроля. Вы всегда можете получить актуальную информацию о чрезмерных тратах, включая даты их совершения, сумму, категорю, валюту и много чего еще.
___
### *Описание* :page_with_curl:
Приложение представляет собой микросервис, состоящий из двух API - одного клиента для обращения к внешнему API сайта <a href="https://twelvedata.com/">Twelve Data</a>, и использующего базу данных для хранения бизнес-сущностей и курсов валют.
Этот сервис: 
1. Получает информацию о каждой расходной операции в тенге **KZT**, рублях **RUB** и других валютах в реальном времени и сохраняет ее в своей собственной базе данных.
2. Хранит месячный лимит по расходам в долларах США **USD** раздельно для двух категорий расходов: товаров и услуг. Если лимит не установлен, принимается равным **1000 USD**.
3. Запрашивает данные биржевых курсов валютных пар **KZT/USD**, **RUB/USD** по дневному интервалу и хранит их в собственной базе данных. При расчете курсов использовать данные закрытия **close**.
   В случае, если таковые недоступны на текущий день (выходной или праздничный день), то используются данные последнего закрытия **previous_close**
5. Помечает транзакции, превысившие месячный лимит операций **limit_exceeded**
6. Дает возможность клиенту установить новый лимит. При установлении нового лимита микросервисом автоматически выставляется текущая дата, не позволяя выставить ее в прошедшем или будущем времени. Не позволяет обновлять существующие лимиты
7. По запросу клиента возвращается список транзакций, превысивших лимит, с указанием лимита, который был превышен (дата установления, сумма лимита, валюта **USD**).
___
### *Используемые инструменты и технологии:* :computer:

#### *Java:*
+ BigDecimal, Date types, enums 
+ Collections, equals and hashCode, Stream API, Lambdas 
+ Обработка исключений
+ ООП
+ Code conventions 
+ Multithreading
                                                                                                   
 #### *Фреймворки, библиотеки и Build Tools:*
+ Spring Web: бины, компоненты, конфигурации
+ Spring Validaion
+ Конфигурации Spring Boot автоконфигурация с исключениями
+ ORM (Spring Data JPA, Hibernate): Entities, Transactions, native queries, data mappings
+ PostgreSQL, нормальные формы, отношения таблиц, внешние ключи, подзапросы
+ DTO-Entity mappers
+ Maven (Bill of Materials, dependency management, build plugins)
+ Инструмент миграции Liquibase
+ Логирование slf4j, MDC
+ Swagger UI
                                                                                                      
#### *Docker*
+ Dockerfile
+ docker-compose

#### *Тестирование:*
+ Testcontainers
+ Mockito
+ Jacoco
___
### *Инструкция по запуску:* :rocket:
Существует большое множество способов запустить этот проект, я расскажу лишь об одном из них. Самом простом, на мой взгляд, но он предполагает, что у Вас локально установлены такие технологии, как  ***Docker*** и ***Git***. Итак, нужно выполнить 4 шага:
1. Откройте CMD или её аналог (или Терминал, на Mac), и выполните команду, чтобы склонировать этот репозиторий:
```git
git clone https://github.com/evgeniy-fedorchenko/ExpensesAssistant.git
```
2. Затем нужно загрузить образ базы данных:
```git
docker pull postgres
```
3. Перейти в нужную папку с помощью:
```git
cd expAssistant/src/main/docker
```
4. И наконец запустить процесс развертывания приложения на Вашей машине локально:
```git
docker-compose up
```
**Готово!** Теперь посетите сайт http://localhost:8080/swagger-ui/index.html#/, чтобы повзаимодействовать с приложением через графический интерфейс. Более подробно ознакомиться с функционалом Вы можете используя ***Javadoc*** или непосредственно из кода :)
## ***Спасибо*** :ok_hand:
