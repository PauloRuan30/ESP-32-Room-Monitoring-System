from locust import HttpUser, task, between
import random

class MyUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def send_data(self):
        self.client.post("/api/data", json={
            "temperature": round(random.uniform(20.0, 30.0), 2),
            "humidity": round(random.uniform(40.0, 60.0), 2),
            "gas": random.randint(200, 800)
        })
