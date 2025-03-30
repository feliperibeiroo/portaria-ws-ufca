-- apl_catraca.condominios definição

-- Drop table

-- DROP TABLE apl_catraca.condominios;

CREATE TABLE IF NOT EXISTS apl_catraca.condominios (
	id bigserial NOT NULL,
	nome varchar(255) NULL,
	"local" text NULL,
	img varchar(255) NULL,
	CONSTRAINT condominios_pkey PRIMARY KEY (id)
);


-- apl_catraca.visitantes definição

-- Drop table

-- DROP TABLE apl_catraca.visitantes;

CREATE TABLE IF NOT EXISTS apl_catraca.visitantes (
	id bigserial NOT NULL,
	nome_completo varchar(255) NULL,
	cpf varchar(255) NULL,
	"version" int8 DEFAULT 1 NOT NULL,
	CONSTRAINT cpf_unique UNIQUE (cpf),
	CONSTRAINT visitantes_pkey PRIMARY KEY (id)
);


-- apl_catraca.visitas definição

-- Drop table

-- DROP TABLE apl_catraca.visitas;

CREATE TABLE IF NOT EXISTS apl_catraca.visitas (
	id bigserial NOT NULL,
	hor_inicio timestamp NULL,
	hor_final timestamp NULL,
	motivo text NULL,
	autorizada bool NULL,
	hor_atendimento timestamp(6) NULL,
	anfitriao varchar(255) NULL,
	hor_solicitacao timestamp NULL,
	id_empresa int8 NULL,
	"version" int8 DEFAULT 1 NOT NULL,
	id_atendente varchar(255) NULL,
	CONSTRAINT visitas_pkey PRIMARY KEY (id)
);


-- apl_catraca.empresas definição

-- Drop table

-- DROP TABLE apl_catraca.empresas;

CREATE TABLE IF NOT EXISTS apl_catraca.empresas (
	id bigserial NOT NULL,
	nome varchar(255) NULL,
	id_condominio int8 NULL,
	CONSTRAINT empresas_pkey PRIMARY KEY (id),
	CONSTRAINT fk_condominio_id FOREIGN KEY (id_condominio) REFERENCES apl_catraca.condominios(id)
);


-- apl_catraca.visita_visitante definição

-- Drop table

-- DROP TABLE apl_catraca.visita_visitante;

CREATE TABLE IF NOT EXISTS apl_catraca.visita_visitante (
	id_visita int8 NOT NULL,
	id_visitante int8 NOT NULL,
	is_acompanhante bool NULL,
	qrcode varchar(255) NULL,
	CONSTRAINT visita_visitante_pkey PRIMARY KEY (id_visita, id_visitante),
	CONSTRAINT id_visita_fk FOREIGN KEY (id_visita) REFERENCES apl_catraca.visitas(id),
	CONSTRAINT id_visitante_fk FOREIGN KEY (id_visitante) REFERENCES apl_catraca.visitantes(id)
);