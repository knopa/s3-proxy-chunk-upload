create table s3files (
  id                        varchar(255) not null,
  name                      varchar(255),
  status                    integer,
  convert_status            integer DEFAULT 0,
  job_id                    varchar(100) DEFAULT NULL,
  url                       varchar(255),
  created                   timestamp,
  parts                     TEXT,
  convert_response          text DEFAULT NULL,
  proccess                  varchar (255) DEFAULT NULL,
  constraint pk_s3files primary key (id))
;

create sequence s3files_seq;