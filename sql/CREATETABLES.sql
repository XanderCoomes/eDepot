CREATE TABLE PRODUCTS (
    stock_num CHAR(7),
    location_id VARCHAR(50),
    manufacturer VARCHAR(50),
    model_number VARCHAR(50), 
    quantity INT,
    min_stock_level INT, 
    max_stock_level INT,
    replenishment INT, 
    PRIMARY KEY (stock_num), 
    UNIQUE (manufacturer, model_number)
);

CREATE TABLE SHIPNOTICES(
    notice_id VARCHAR(50),
    carrier VARCHAR(50), 
    notice_date DATE,
    received_date DATE,
    shipping_status VARCHAR(50),
    PRIMARY KEY(notice_id)
);

CREATE TABLE SHIPITEMS(
    manufacturer VARCHAR(50), 
    model_number VARCHAR(50), 
    quantity INT, 
    notice_id VARCHAR(50), 
    PRIMARY KEY(notice_id, manufacturer, model_number), 
    FOREIGN KEY(manufacturer, model_number) REFERENCES PRODUCTS(manufacturer, model_number), 
    FOREIGN KEY(notice_id) REFERENCES SHIPNOTICES(notice_id)
)


