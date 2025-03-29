# Firebase Setup for Inventory Management

This document provides instructions for setting up Firebase Firestore for the Inventory Management module of the ERP system.

## Initialization

The app automatically initializes the necessary Firebase collections when it first starts. This setup includes:

1. Creating the `products` collection with a sample document
2. Creating the `vendors` collection with a sample document 
3. Setting up the appropriate schema for both collections

## Manual Setup Steps

If you need to manually set up Firebase, follow these steps:

### 1. Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project" and follow the setup wizard
3. Once created, add an Android app to your project using the package name `com.erp`
4. Download the `google-services.json` file and place it in the `app/` directory of your project

### 2. Set Up Firestore Database

1. In the Firebase Console, go to "Firestore Database"
2. Click "Create Database"
3. Choose "Start in production mode" and select a location close to your users

### 3. Upload Security Rules

The security rules for Firestore are defined in `app/src/main/assets/firestore.rules`. To upload them:

1. Install the Firebase CLI by running `npm install -g firebase-tools`
2. Login to Firebase: `firebase login`
3. Initialize your project: `firebase init firestore`
4. Copy the contents of `firestore.rules` to the generated rules file
5. Deploy the rules: `firebase deploy --only firestore:rules`

### 4. Create Indexes

The necessary indexes are defined in `app/src/main/assets/firestore.indexes.json`. To set them up:

1. Make sure you've initialized your project with the Firebase CLI
2. Copy the contents of `firestore.indexes.json` to the generated indexes file
3. Deploy the indexes: `firebase deploy --only firestore:indexes`

## Important Collections

### Products Collection

The `products` collection stores inventory items with the following fields:

- `id`: Unique identifier (String)
- `name`: Product name (String)
- `description`: Product description (String)
- `sku`: Stock keeping unit code (String)
- `price`: Product price (Double)
- `category`: Product category (String, enum value)
- `status`: Product status (String, enum value)
- `stockQuantity`: Current stock level (Integer)
- `reorderLevel`: Level at which to reorder stock (Integer)
- `vendorId`: ID of the vendor (String)
- `lastRestockDate`: Date of last restock (Timestamp)
- `createdAt`: Creation date (Timestamp)
- `updatedAt`: Last update date (Timestamp)

### Vendors Collection

The `vendors` collection stores information about suppliers with the following fields:

- `id`: Unique identifier (String)
- `name`: Vendor name (String)
- `email`: Contact email (String)
- `phone`: Contact phone number (String)
- `address`: Street address (String)
- `city`: City (String)
- `state`: State or province (String)
- `country`: Country (String)
- `postalCode`: Postal or ZIP code (String)
- `contactPerson`: Name of primary contact (String)
- `notes`: Additional notes (String)
- `status`: Vendor status (String, enum value)
- `rating`: Vendor rating (Double)
- `createdAt`: Creation date (Timestamp)
- `updatedAt`: Last update date (Timestamp)

## Troubleshooting

If you encounter issues with Firebase initialization:

1. Check if your device/emulator has an active internet connection
2. Verify that the `google-services.json` file is correctly placed in the `app/` directory
3. Make sure you have the necessary dependencies in your `build.gradle` files
4. Check the logcat output for Firebase-related errors
5. Verify that your Firebase project has Firestore enabled

For more information, refer to the [Firebase documentation](https://firebase.google.com/docs/firestore). 