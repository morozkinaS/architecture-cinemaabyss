## Изучите [README.md](.\README.md) файл и структуру проекта.

# Задание 1

1.   [Диаграмма To-Be архитектуры](./docs/cinemaabyss-to-be.drawio)

## Задание 2. Реализация прокси-сервиса

### Часть 1. Реализация сервиса

**Реализация прокси-сервиса:**

Прокси-сервис реализован на Java с использованием Spring Boot и WebClient для маршрутизации запросов. Сервис выполняет роль API Gateway и обрабатывает следующие эндпоинты:

- `/health` - проверка здоровья прокси-сервиса
- `/api/movies` (GET/POST) - маршрутизация с фиче-флагом миграции
- `/api/users` (GET/POST) - всегда направляется в монолит
- `/api/payments` (GET/POST) - всегда направляется в монолит
- `/api/subscriptions` (GET/POST) - всегда направляется в монолит
- `/api/events/*` - всегда направляется в микросервис событий

**Фиче-флаг миграции:**

Реализован паттерн Strangler Fig для постепенного переключения трафика:
- Переменная окружения `MOVIES_MIGRATION_PERCENT` определяет процент запросов, направляемых в новый микросервис
- При значении 0 - 100% трафика идет в монолит
- При значении 100 - 100% трафика идет в микросервис movies
- При промежуточных значениях - случайное распределение запросов
- Переменная `GRADUAL_MIGRATION` позволяет включить/выключить постепенную миграцию

**Результаты тестирования:**

Все тесты Postman проходят успешно. Прокси-сервис корректно маршрутизирует запросы между монолитом и микросервисами.

**Тестирование миграции:**

При изменении `MOVIES_MIGRATION_PERCENT` в docker-compose.yml:
- При значении 0: все запросы `/api/movies` логируются как "Routing to: MONOLITH"
- При значении 100: все запросы логируются как "Routing to: NEW movies service"
- При значении 50: наблюдается приблизительно равное распределение между монолитом и новым сервисом

### Часть 2. Реализация Kafka

**Реализация events service:**

Микросервис событий реализован как MVP для проверки гипотезы использования Kafka в архитектуре:

- **Producer**: При получении HTTP запроса создает событие и отправляет в соответствующий топик Kafka
- **Consumer**: Асинхронно читает сообщения из топиков и логирует их обработку
- **Топики Kafka**:
  - `movie-events` - события просмотра/оценки фильмов
  - `user-events` - события регистрации/авторизации пользователей
  - `payment-events` - события успешных платежей

**API events service:**
- `POST /api/events/movie` - создание события фильма (возвращает 201 Created)
- `POST /api/events/user` - создание события пользователя (возвращает 201 Created)
- `POST /api/events/payment` - создание события платежа (возвращает 201 Created)
- `GET /api/events/health` - проверка сервиса

Все эндпоинты возвращают статус 201 Created и информацию о партиции/смещении в Kafka.

**Результаты тестирования:**

[Скриншот тестов Postman](screenshots/postman-tests-task2.png)

*Все тесты проходят успешно*

**Состояние топиков Kafka:**

[Скриншот Kafka UI](screenshots/kafka-ui-task2.png)

# Задание 3

Команда начала переезд в Kubernetes для лучшего масштабирования и повышения надежности. 
Вам, как архитектору осталось самое сложное:
 - реализовать CI/CD для сборки прокси сервиса
 - реализовать необходимые конфигурационные файлы для переключения трафика.


### CI/CD
Деплой доработан. Сборка и тесты прошли успешно
- ✅ Сборка прошла успешно
[Сборка](screenshots/build.png)

- ✅ Все тесты выполнены успешно
[Тесты](screenshots/api-tests.png)

Workflow `.github/workflows/docker-build-push.yml` доработан для сборки и публикации образов `proxy-service` и `events-service` в GitHub Container Registry.

**Изменения в workflow:**
- Добавлены шаги для сборки и пуша `proxy-service` и `events-service`
- Workflow запускается при пуше в ветку `cinema`

**Результаты CI/CD:**
- ✅ Образы опубликованы в GitHub Packages


Образы доступны в GitHub Packages:
- `ghcr.io/morozkinas/architecture-cinemaabyss/proxy-service:latest`
- `ghcr.io/morozkinas/architecture-cinemaabyss/events-service:latest`
- `ghcr.io/morozkinas/architecture-cinemaabyss/movies-service:latest`
- `ghcr.io/morozkinas/architecture-cinemaabyss/monolith:latest`

### Proxy в Kubernetes


- Создан Personal Access Token (PAT) с правами `read:packages` и `write:packages`
- Выполнен логин в GitHub Container Registry
- Настроено развертывание в Kubernetes с поддержкой фиче-флага миграции, добавлены все необходимые доработки в файлах.

- [Скриншот](screenshots/call-api-movies.png) вывода при вызове https://cinemaabyss.example.com/api/movies
- [Скриншот](screenshots/events-logs.png)  вывода event-service после вызова тестов.


# Задание 4
Для простоты дальнейшего обновления и развертывания вам как архитектуру необходимо так же реализовать helm-чарты для прокси-сервиса и проверить работу 

Для этого:
1. Файлы отредактированы, проставлены пути, секреты

2. Заполнены шаблоны для proxy-service.yaml и events-service.yaml 

3. [Скриншот развертывания helm](screenshots/helm-pods.png)
4. 
5. [Скриншот вывода https://cinemaabyss.example.com/api/movies](screenshots/api-movies-ps.png) в powershell
6. [Скриншот вывода https://cinemaabyss.example.com/api/movies](screenshots/api-movies-browser.png) в браузере

## Удаляем все

```bash
kubectl delete all --all -n cinemaabyss
kubectl delete namespace cinemaabyss
```
