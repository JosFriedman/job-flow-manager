DROP TABLE JOB_STATUS;

CREATE TABLE JOB_STATUS 
(
ID  NUMBER(19,0),
APP_ID VARCHAR2(64 BYTE),
JOB_ID VARCHAR2(64 BYTE),
DESCRIPTION VARCHAR2(1024 BYTE),
JOB_CREATED TIMESTAMP (6), 
STATUS VARCHAR2(32 BYTE),
START_TIMESTAMP TIMESTAMP (6), 
END_TIMESTAMP TIMESTAMP (6), 
MULTI_INSTANCE_CTRL NUMBER(4,0) DEFAULT 0, 
ERROR_COUNT NUMBER(4,0) DEFAULT 0, 
ERROR_REASON VARCHAR2(1024 BYTE)
);

