create TABLE USERS (
    id serial primary key,
    email varchar(255) unique,
    username varchar(255),
    photourl varchar(255),
    googleId varchar(100),
    created date,
    active boolean
);

create TABLE WORDS (
    id serial primary key,
    english varchar(255),
    russian text,
    partOfSpeech char(20),
    url text
);

create TABLE VOCABULARIES (
    id serial primary key,
    name varchar(255),
    complexity double precision default 0.0,
    countOfWords integer default 0,
    description text,
    version varchar(20),
    created date default now(),
    lastModifiedBy date default now()
);

truncate table USERS restart IDENTITY;
truncate table WORDS restart IDENTITY;
truncate table VOCABULARIES restart IDENTITY;


create TABLE VOCABULARY_WORDS (
    id serial primary key,
    vocabulary int,
    word int,
    constraint VOCABULARY_WORDS_VOC_FK foreign key (vocabulary) references VOCABULARIES(id),
    constraint VOCABULARY_WORDS_WORD_FK foreign key (word) references WORDS(id)
);

truncate table VOCABULARY_WORDS restart IDENTITY;


