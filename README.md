# 🗂️ Task Tracker API

**Task Tracker** — это backend-приложение для управления проектами, задачами и их статусами. Реализовано с использованием Spring Boot, JWT-аутентификации и REST API.

## 🚀 Стек технологий

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven
- Docker + Docker Compose
- Lombok

---

## ⚙️ Сборка и запуск

### 🧱 Локально

```bash
# Сборка jar-файла
mvn clean package

# Запуск
java -jar target/<file.name>.jar

🐳 С Docker

# Сборка образа
docker build -t task-tracker-app .

# Запуск
docker-compose up -d
```
---
````
📁 Структура проекта

src
├── auth                     # Аутентификация и авторизация (JWT)
├── controller               # REST-контроллеры
├── dto                      # DTO-объекты
├── entity                   # JPA-сущности
├── repository               # Репозитории Spring Data
├── service                  # Бизнес-логика
└── config                   # Конфигурация (безопасность, Web, и т. д.)
 ````
---
````
- # 🔐 Аутентификация

- Используется JWT. После регистрации и логина клиент получает access token и использует его в заголовке:

- Authorization: Bearer <токен>
````
---

### 📌 API
## Аутентификация
````
Метод	    Endpoint	           Описание
POST	    /api/auth/register	   Регистрация
POST	    /api/auth/login	   Логин
POST        /api/auth/logout       Выход
````
---

## Проекты
````
Метод	    Endpoint	            Описание
GET	    /api/projects	    Получить список проектов
POST	    /api/projects	    Создать проект
PATCH	    /api/projects/{id}	    Обновить проект
DELETE	    /api/projects/{id}	    Удалить проект
````
---

## Состояния задач
````
Метод	Endpoint	                        Описание
GET	/api/projects/{projectId}/task-states	Получить состояния
POST	/api/projects/{projectId}/task-states	Создать состояние
PATCH	/api/task-states/{taskStateId}	        Обновить состояние
DELETE	/api/task-states/{taskStateId}	        Удалить состояние
````
---
## Задачи
````
Метод	Endpoint	Описание
GET	/api/task-states/{taskStateId}/tasks	Получить задачи
POST	/api/task-states/{taskStateId}/task	Создать задачу
PATCH	/api/task/{taskId}	                Обновить задачу
DELETE	/api/task/{taskId}	                Удалить задачу
````

