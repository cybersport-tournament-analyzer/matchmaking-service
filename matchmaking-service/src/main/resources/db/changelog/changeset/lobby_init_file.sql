CREATE TABLE lobby (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    mode VARCHAR(10) NOT NULL
);

CREATE TABLE lobby_team1 (
    lobby_id UUID NOT NULL,
    steam_id VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    hours_played BIGINT,
    rating_elo BIGINT,
    faceit_winrate BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    role VARCHAR(50),
    FOREIGN KEY (lobby_id) REFERENCES lobby(id) ON DELETE CASCADE
);

CREATE TABLE lobby_team2 (
    lobby_id UUID NOT NULL,
    steam_id VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    hours_played BIGINT,
    rating_elo BIGINT,
    faceit_winrate BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    role VARCHAR(50),
    FOREIGN KEY (lobby_id) REFERENCES lobby(id) ON DELETE CASCADE
);
