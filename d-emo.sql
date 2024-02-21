create table user
(
    ID       int auto_increment
        primary key,
    mail     varchar(64)   null,
    phone    varchar(32)   null,
    password varchar(128)  null,
    head     varchar(1024) null,
    uName    varchar(64)   null,
    constraint User_pk2
        unique (mail),
    constraint User_pk3
        unique (phone)
)
    comment '用户信息表';

create table chats
(
    senderID   int                                not null,
    receiverID int                                not null,
    sendTime   datetime default CURRENT_TIMESTAMP null,
    messageID  int                                not null,
    message    text                               null,
    type       int      default 0                 null comment '消息类型',
    primary key (senderID, receiverID, messageID),
    constraint chats_user_ID_fk
        foreign key (senderID) references user (ID),
    constraint chats_user_ID_fk2
        foreign key (receiverID) references user (ID)
);

create index chats_senderID_index
    on chats (senderID);

create definer = root@localhost trigger msID_auto_increase_trigger
    before insert
    on chats
    for each row
begin
    declare max_msID int;
    select max(messageID) into max_msID from chats where senderID=new.senderID and receiverID=new.receiverID;
    set new.messageID=ifnull(max_msID, 0) + 1;
end;

create index User_mail_index
    on user (mail);

create index User_phone_index
    on user (phone);

