CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert a default admin user
INSERT INTO users (username, password, name, email)
VALUES ('admin', '$2a$12$0PU0z1YSW9XztEMmP/VUvOSuebjhxbLO0QzSFFrRJsuHwv9Mgy5y6', 'Admin User', 'admin@example.com');

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN'), (1, 'ROLE_USER');