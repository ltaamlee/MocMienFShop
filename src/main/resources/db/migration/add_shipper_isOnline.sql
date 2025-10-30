-- Add isOnline column to Shipper table
-- This column tracks whether a shipper is currently online and available to receive orders

ALTER TABLE Shipper 
ADD isOnline BIT NOT NULL DEFAULT 0;

-- Update existing shippers to be offline by default
UPDATE Shipper SET isOnline = 0 WHERE isOnline IS NULL;

