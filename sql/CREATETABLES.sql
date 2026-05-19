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


