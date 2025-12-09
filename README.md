# job-scheduler-service

Spring Boot 3.5 / Java 21 microservice with a **Redis-backed distributed lock**
ensuring that a **cron scheduler runs only on a single instance**, even when the
service is scaled to multiple replicas on Kubernetes.

## Features

- Java 21, Spring Boot 3.5.8, Gradle (Groovy)
- Cron scheduler with `@Scheduled`
- Redis-based distributed lock (`StringRedisTemplate` + Lua script)
- Safe leader election per job execution (only one pod runs the job)
- Health / metrics via Spring Boot Actuator
- Dockerfile & Kubernetes manifests (5 replicas example)
- Local development via `docker-compose` with Redis

## Architecture

- **Kubernetes**: one `Deployment` with multiple replicas (e.g. 5) +
  `Service` for traffic.
- **Redis**: shared instance / cluster used for distributed locking.
- Each pod has the same `@Scheduled` job, but **before running** it tries to
  acquire a Redis lock (`SETNX` with TTL).
- Only the pod that **successfully acquires the lock** executes the job.
- TTL protects against stale locks if a pod dies during execution.

Packages:

- `com.github.dimitryivaniuta.scheduler` – Spring Boot entrypoint
- `com.github.dimitryivaniuta.scheduler.config` – instance ID configuration
- `com.github.dimitryivaniuta.scheduler.lock` – Redis lock abstraction
- `com.github.dimitryivaniuta.scheduler.job` – scheduled job(s)

## Configuration

`application.yml`:

```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

scheduler:
  my-job:
    cron: "0 */5 * * * *"
    lock-ttl: 300000
```