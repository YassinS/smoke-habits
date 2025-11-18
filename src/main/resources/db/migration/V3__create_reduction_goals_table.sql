-- Create reduction_goals table
CREATE TABLE IF NOT EXISTS reduction_goals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    starting_cigarettes_per_day INTEGER NOT NULL,
    target_cigarettes_per_day INTEGER NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    duration_in_days INTEGER NOT NULL,
    strategy VARCHAR(20) NOT NULL DEFAULT 'LINEAR',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    daily_reduction_rate DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_reduction_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT check_valid_reduction CHECK (target_cigarettes_per_day < starting_cigarettes_per_day),
    CONSTRAINT check_valid_duration CHECK (duration_in_days > 0),
    CONSTRAINT check_valid_target CHECK (target_cigarettes_per_day >= 0),
    CONSTRAINT check_valid_starting CHECK (starting_cigarettes_per_day > 0)
);

-- Create index for common queries
CREATE INDEX IF NOT EXISTS idx_reduction_goals_user_id ON reduction_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_reduction_goals_user_status ON reduction_goals(user_id, status);
CREATE INDEX IF NOT EXISTS idx_reduction_goals_status ON reduction_goals(status);
