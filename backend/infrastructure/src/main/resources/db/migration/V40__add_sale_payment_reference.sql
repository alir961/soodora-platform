-- The Stripe Checkout session a sale comes from, so a webhook retry cannot record it twice.
ALTER TABLE sales
    ADD COLUMN payment_reference VARCHAR(255) NULL;

ALTER TABLE sales
    ADD CONSTRAINT uc_sales_payment_reference UNIQUE (payment_reference);
