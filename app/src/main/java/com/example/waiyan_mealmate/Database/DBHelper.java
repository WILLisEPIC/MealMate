package com.example.waiyan_mealmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.waiyan_mealmate.R;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    public DBHelper(Context context) {
        super(context, "Mealmate.db", null, 1);
        this.context = context;
    }

    //database creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE User(UserID INTEGER PRIMARY KEY AUTOINCREMENT,Username TEXT,Email TEXT,Password TEXT,Profile BLOB)");
        db.execSQL("CREATE TABLE Meal(MealID INTEGER PRIMARY KEY AUTOINCREMENT,MealName TEXT,Photo BLOB,Type Text,UserID INTEGER,FOREIGN KEY (UserID) REFERENCES User(UserID))");
        db.execSQL("CREATE TABLE Ingredient(IngredientID INTEGER PRIMARY KEY AUTOINCREMENT,IngredientName TEXT,Amount TEXT,Type TEXT,MealID INTEGER,FOREIGN KEY (MealID) REFERENCES Meal(MealID))");
        db.execSQL("CREATE TABLE Preparation(PreparationID INTEGER PRIMARY KEY AUTOINCREMENT,PreparationDetail TEXT,MealID INTEGER,FOREIGN KEY (MealID) REFERENCES Meal(MealID))");
        db.execSQL("CREATE TABLE UserMeal(UserMealID INTEGER PRIMARY KEY AUTOINCREMENT,Status INTEGER DEFAULT 0,UserID INTEGER,MealID INTEGER,FOREIGN KEY (UserID) REFERENCES User(UserID),FOREIGN KEY (MealID) REFERENCES Meal(MealID))");
        db.execSQL("CREATE TABLE UserIngredient(UserIngredientID INTEGER PRIMARY KEY AUTOINCREMENT,IngredientID INTEGER,Status INTEGER DEFAULT 0,UserID INTEGER,FOREIGN KEY (IngredientID) REFERENCES Ingredient(IngredientID),FOREIGN KEY (UserID) REFERENCES User(UserID))");
        onCreateMeals(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Meal");
        db.execSQL("DROP TABLE IF EXISTS Ingredient");
        db.execSQL("DROP TABLE IF EXISTS Preparation");
        db.execSQL("DROP TABLE IF EXISTS UserMeal");
        db.execSQL("DROP TABLE IF EXISTS UserIngredient");
    }

    //Insert User Data
    public boolean insertUserData(String Username,String Email,String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", Username);
        contentValues.put("Email", Email);
        contentValues.put("Password", Password);

        long result = db.insert("User", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Update User Data
    public boolean updateUserData(int UserID,String Username,String Email,String Password,byte[] Profile){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Username", Username);
        contentValues.put("Email", Email);
        contentValues.put("Password", Password);
        contentValues.put("Profile", Profile);
        Cursor cursor=db.rawQuery("SELECT * FROM User WHERE UserID=?", new String[]{String.valueOf(UserID)});
        if(cursor.getCount()>0) {
            long result = db.update("User", contentValues, "UserID=?", new String[]{String.valueOf(UserID)});
            cursor.close();
            if (result == -1) return false;
            else return true;
        }else {
            cursor.close();
            return false;
        }
    }

    //Select User Data
    public Cursor selectUserData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM User WHERE Email = ? AND Password = ?";
        Cursor cursor = db.rawQuery(query, new String[] {email, password});
        return cursor;
    }

    //Select User Data By ID
    public Cursor selectUserDataByID(int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM User WHERE UserID = ?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(UserID)});
        return cursor;
    }

    //Select User Data By email
    public Cursor selectUserDataByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM User WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(email)});
        return cursor;
    }

    //Select Meal Data
    public Cursor selectMealData(int MealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE MealID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(MealID)});
        return cursor;
    }

    //Select Main course Data
    public Cursor selectMainCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE Type = ?";
        Cursor cursor = db.rawQuery(query,new String[] {"Main Course"});
        return cursor;
    }

    //Select Soup Data
    public Cursor selectSoupData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE Type = ?";
        Cursor cursor = db.rawQuery(query,new String[] {"Soup"});
        return cursor;
    }

    //Select Salad Data
    public Cursor selectSaladData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE Type = ?";
        Cursor cursor = db.rawQuery(query,new String[] {"Salad"});
        return cursor;
    }

    //Select Dessert Data
    public Cursor selectDessertData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE Type = ?";
        Cursor cursor = db.rawQuery(query,new String[] {"Dessert"});
        return cursor;
    }

    //Select MealPlan Data
    public Cursor selectUserMealData(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM UserMeal WHERE UserID = ? AND MealID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(UserID), String.valueOf(MealID)});
        return cursor;
    }

    //Select User Created Meal Data
    public Cursor selectUserCreatedMealData(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE UserID = ? AND MealID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(UserID), String.valueOf(MealID)});
        return cursor;
    }

    //Insert MealPlan Data
    public Boolean insertUserMeal(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UserID", UserID);
        contentValues.put("MealID", MealID);

        long result = db.insert("UserMeal", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Delete Meal Data
    public Boolean deleteMeal(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Meal", "MealID = ? AND UserID = ?",new String[]{String.valueOf(MealID), String.valueOf(UserID)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Delete Ingredient Data
    public Boolean deleteIngredient(int MealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Ingredient", "MealID = ?",new String[]{String.valueOf(MealID)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Delete Preparation Data
    public Boolean deletePreparation(int MealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Preparation", "MealID = ?",new String[]{String.valueOf(MealID)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Delete User Meal Data
    public Boolean deleteUserMeal(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("UserMeal", "MealID = ? AND UserID = ?",new String[]{String.valueOf(MealID), String.valueOf(UserID)});

        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Select Meal Ingredient
    public Cursor selectMealIngredient(int MealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Ingredient WHERE MealID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(MealID)});
        return cursor;
    }

    //Select Meal Preparation
    public Cursor selectMealPreparation(int MealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Preparation WHERE MealID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(MealID)});
        return cursor;
    }

    //Select User Meal
    public Cursor selectUserCreatedMeal(int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Meal WHERE UserID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(UserID)});
        return cursor;
    }

    //Select User Selected Meal
    public Cursor selectAllUserMealData(int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM UserMeal WHERE UserID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(UserID)});
        return cursor;
    }

    //Select User Meal by MealID
    public Cursor selectUserMealbyMealID(int MealID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM UserMeal WHERE MealID = ? AND UserID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(MealID) ,String.valueOf(UserID)});
        return cursor;
    }

    //Update Status of UserMeal
    public boolean updateUserMealStatus(int MealID, int UserID,  int Status){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Status", Status);
        Cursor cursor=db.rawQuery("SELECT * FROM UserMeal WHERE MealID = ? AND UserID = ?", new String[]{String.valueOf(MealID), String.valueOf(UserID)});
        if(cursor.moveToFirst()) {
            long result = db.update("UserMeal", contentValues, "MealID = ? AND UserID = ?", new String[]{String.valueOf(MealID), String.valueOf(UserID)});
            if (result == -1) return false;
            else return true;
        }
        cursor.close();
        return false;
    }

    //Update Status of UserIngredient
    public boolean updateUserIngredientStatus(int IngredientID, int Status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Status", Status);

        int check = db.update("UserIngredient", contentValues, "IngredientID = ?", new String[]{String.valueOf(IngredientID)});

        return check > 0;
    }

    //Insert Ingredient Data to User Ingredient
    public Boolean insertUserIngredient(int IngredientID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IngredientID", IngredientID);
        contentValues.put("UserID", UserID);

        long result = db.insert("UserIngredient", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Insert Ingredient Data to User Ingredient
    public Boolean deleteUserIngredient(int IngredientID, int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("UserIngredient", "IngredientID = ? AND UserID = ?", new String[]{String.valueOf(IngredientID), String.valueOf(UserID)});

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Insert Meal Data
    public long insertMealData(String Name,byte[] Photo,String Type,int UserID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MealName", Name);
        contentValues.put("Photo", Photo);
        contentValues.put("Type", Type);
        if (UserID != 0) {
            contentValues.put("UserID", UserID);
        } else {
            contentValues.putNull("UserID");
        }
        long mealID = db.insert("Meal", null, contentValues);

        return mealID;
    }

    //Insert Ingredient Data
    public boolean insertIngredientData(String Ingredient,String Amount,String Type,int MealID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IngredientName", Ingredient);
        contentValues.put("Amount", Amount);
        contentValues.put("Type", Type);
        contentValues.put("MealID", MealID);
        long result = db.insert("Ingredient", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Insert Preparation Data
    public boolean insertPreparationData(String Preparation,int MealID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PreparationDetail", Preparation);
        contentValues.put("MealID", MealID);
        long result = db.insert("Preparation", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Select User Selected Meal
    public Cursor selectUserIngredient(int UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM UserIngredient WHERE UserID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(UserID)});
        return cursor;
    }

    //Select User Selected Meal
    public Cursor selectIngredientByType(int IngredientId, String Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Ingredient WHERE IngredientID = ? AND Type = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(IngredientId), Type});
        return cursor;
    }

    //Select User Selected Meal
    public Cursor selectIngredient(int IngredientID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Ingredient WHERE IngredientID = ?";
        Cursor cursor = db.rawQuery(query,new String[] {String.valueOf(IngredientID)});
        return cursor;
    }

    //Meals Insertion
    public void onCreateMeals(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            //using object array for both string and integer data
            Object[][] meals = {
                    //Main Courses
                    {"Creamy Spinach Chicken", R.drawable.creamy_spinach_chicken, "Main Course"},
                    {"Chicken Chow Mein", R.drawable.chicken_chow_mein, "Main Course"},
                    {"Spaghetti Carbonara", R.drawable.spaghetti_carbonara, "Main Course"},
                    {"Grilled Salmon", R.drawable.grilled_salmon, "Main Course"},
                    {"Beef Stroganoff", R.drawable.beef_stroganoff, "Main Course"},
                    {"Vegetable Stir Fry", R.drawable.vegetable_stir_fry, "Main Course"},

                    //Soups
                    {"Thai-inspired Coconut Chicken Soup", R.drawable.thai_inspired_coconut_chicken_soup, "Soup"},
                    {"Tomato Basil Soup", R.drawable.tomato_basil_soup, "Soup"},
                    {"Carrot Ginger Soup", R.drawable.carrot_ginger_soup, "Soup"},
                    {"Fish Soup", R.drawable.fish_soup, "Soup"},
                    {"Mushroom Soup", R.drawable.mushroom_soup, "Soup"},
                    {"Lentil Soup", R.drawable.lentil_soup, "Soup"},

                    //Salads
                    {"Greek Salad", R.drawable.greek_salad, "Salad"},
                    {"Broccoli Salad", R.drawable.broccoli_salad, "Salad"},
                    {"Caesar Salad", R.drawable.caesar_salad, "Salad"},
                    {"Fruit Salad", R.drawable.fruit_salad, "Salad"},
                    {"Cobb Salad", R.drawable.cobb_salad, "Salad"},
                    {"Quinoa Salad", R.drawable.quinoa_salad, "Salad"},

                    // Desserts
                    {"Chocolate Brownie", R.drawable.chocolate_brownie, "Dessert"},
                    {"Cheesecake", R.drawable.cheesecake, "Dessert"},
                    {"Tiramisu", R.drawable.tiramisu, "Dessert"},
                    {"Apple Pie", R.drawable.apple_pie, "Dessert"},
                    {"Panna Cotta", R.drawable.panna_cotta, "Dessert"}
            };

            String[][][] ingredients = {
                    //Main Courses
                    //Creamy Spinach Chicken
                    {{"Unsalted Butter", "30g", "Dairy"}, {"Spinach", "240g", "Vegetable"}, {"Chicken Breasts", "4", "Protein"}, {"Garlic Cloves", "4", "Vegetable"}, {"Double Cream", "200ml", "Dairy"}, {"Parmigiano Reggiano", "40g", "Dairy"}},
                    //Chicken Chow Mein
                    {{"Chicken Breast", "175g", "Protein"}, {"Egg White", "1", "Protein"}, {"Vegetable Oil", "2 tbsp", "Oil"}, {"Shallot", "1", "Vegetable"}, {"Garlic Cloves", "4", "Vegetable"}, {"Egg Noodles", "2", "Grain"}, {"Soy Sauce", "2 tbsp", "Spice"}},
                    //Spaghetti Carbonara
                    {{"Spaghetti", "400g", "Grain"}, {"Egg Yolks", "4", "Protein"}, {"Parmigiano Reggiano", "60g", "Dairy"}, {"Guanciale", "150g", "Protein"}, {"Black Pepper", "to taste", "Spice"}, {"Salt", "to taste", "Spice"}},
                    //Grilled Salmon
                    {{"Salmon Fillets", "4", "Protein"}, {"Olive Oil", "2 tbsp", "Oil"}, {"Lemon", "1 (juice and zest)", "Vegetable"}, {"Garlic Cloves", "2 (minced)", "Vegetable"}, {"Salt", "to taste", "Spice"}, {"Black Pepper", "to taste", "Spice"}},
                    //Beef Stroganoff
                    {{"Beef", "500g (sliced)", "Protein"}, {"Mushrooms", "250g", "Vegetable"}, {"Onion", "1 (chopped)", "Vegetable"}, {"Sour Cream", "200ml", "Dairy"}, {"Beef Stock", "500ml", "Stock"}, {"Paprika", "1 tsp", "Spice"}},
                    //Vegetable Stir Fry
                    {{"Broccoli", "200g", "Vegetable"}, {"Carrot", "1 (sliced)", "Vegetable"}, {"Red Pepper", "1 (sliced)", "Vegetable"}, {"Soy Sauce", "2 tbsp", "Spice"}, {"Ginger", "1 tsp (minced)", "Spice"}, {"Sesame Oil", "1 tbsp", "Oil"}},

                    //Soups
                    //Thai-inspired Coconut Chicken Soup
                    {{"Vegetable Oil", "1 tbsp", "Oil"}, {"Shallots", "2", "Vegetable"}, {"Lemongrass", "1 stalk", "Vegetable"}, {"Garlic Cloves", "2", "Vegetable"}, {"Coconut Milk", "400ml", "Dairy"}, {"Chicken Breasts", "2", "Protein"}},
                    //Tomato Basil Soup
                    {{"Tomatoes", "800g (diced)", "Vegetable"}, {"Garlic Cloves", "3 (minced)", "Vegetable"}, {"Onion", "1 (chopped)", "Vegetable"}, {"Vegetable Stock", "750ml", "Stock"}, {"Basil Leaves", "Handful", "Vegetable"}, {"Olive Oil", "2 tbsp", "Oil"}},
                    //Carrot Ginger Soup
                    {{"Carrots", "500g (peeled and chopped)", "Vegetable"}, {"Ginger", "1 tbsp (grated)", "Spice"}, {"Onion", "1 (chopped)", "Vegetable"}, {"Vegetable Stock", "1 liter", "Stock"}, {"Coconut Milk", "200ml", "Dairy"}, {"Olive Oil", "2 tbsp", "Oil"}},
                    //Fish Soup
                    {{"White Fish Fillets", "4", "Protein"}, {"Potatoes", "2 (cubed)", "Vegetable"}, {"Leeks", "1 (chopped)", "Vegetable"}, {"Vegetable Stock", "1.25L", "Stock"}, {"Tomato Purée", "1 tbsp", "Condiment"}, {"Dill", "10g", "Herb"}},
                    //Mushroom Soup
                    {{"Mushrooms", "300g", "Vegetable"}, {"Onion", "1 (chopped)", "Vegetable"}, {"Garlic Clove", "1 (minced)", "Vegetable"}, {"Vegetable Stock", "750ml", "Stock"}, {"Cream", "100ml", "Dairy"}, {"Butter", "2 tbsp", "Dairy"}},
                    //Lentil Soup
                    {{"Red Lentils", "200g", "Grain"}, {"Onion", "1 (chopped)", "Vegetable"}, {"Carrots", "2 (chopped)", "Vegetable"}, {"Garlic Cloves", "2 (minced)", "Vegetable"}, {"Cumin", "1 tsp", "Spice"}, {"Vegetable Stock", "1 liter", "Stock"}},

                    //Salads
                    //Greek Salad
                    {{"Vine Tomatoes", "4", "Vegetable"}, {"Cucumber", "1", "Vegetable"}, {"Red Onion", "0.5", "Vegetable"}, {"Kalamata Olives", "16", "Vegetable"}, {"Feta Cheese", "85g", "Dairy"}, {"Olive Oil", "4 tbsp", "Oil"}},
                    //Broccoli Salad
                    {{"Broccoli", "300g", "Vegetable"}, {"Carrot", "2", "Vegetable"}, {"Dried Cranberries", "50g", "Fruit"}, {"Cashews", "50g", "Nut"}, {"Cider Vinegar", "80ml", "Condiment"}, {"Maple Syrup", "1 tbsp", "Sweetener"}},
                    //Caesar Salad
                    {{"Romaine Lettuce", "1 head", "Vegetable"}, {"Croutons", "100g", "Grain"}, {"Parmesan Cheese", "50g (shaved)", "Dairy"}, {"Caesar Dressing", "100ml", "Condiment"}, {"Chicken Breast", "200g", "Protein"}},
                    //Fruit Salad
                    {{"Apples", "2 (chopped)", "Fruit"}, {"Bananas", "2 (sliced)", "Fruit"}, {"Grapes", "1 cup", "Fruit"}, {"Oranges", "2 (peeled)", "Fruit"}, {"Honey", "2 tbsp", "Sweetener"}, {"Lemon Juice", "1 tbsp", "Condiment"}},
                    //Cobb Salad
                    {{"Lettuce", "1 head", "Vegetable"}, {"Chicken Breast", "1 (grilled)", "Protein"}, {"Avocado", "1 (sliced)", "Vegetable"}, {"Bacon", "4 slices (crispy)", "Protein"}, {"Boiled Egg", "2", "Protein"}, {"Blue Cheese", "50g", "Dairy"}},
                    //Quinoa Salad
                    {{"Quinoa", "150g (cooked)", "Grain"}, {"Cherry Tomatoes", "1 cup", "Vegetable"}, {"Cucumber", "1 (diced)", "Vegetable"}, {"Feta Cheese", "100g", "Dairy"}, {"Lemon Juice", "2 tbsp", "Condiment"}, {"Parsley", "2 tbsp (chopped)", "Herb"}},

                    //Desserts
                    //Chocolate Lava Cake
                    {{"Dark Chocolate", "200g", "Dairy"}, {"Unsalted Butter", "100g", "Dairy"}, {"Eggs", "4", "Protein"}, {"Egg Yolks", "2", "Protein"}, {"Sugar", "150g", "Sweetener"}, {"All-purpose Flour", "50g", "Grain"}, {"Vanilla Extract", "1 tsp", "Spice"}, {"Salt", "Pinch", "Spice"}},
                    //Cheesecake
                    {{"Digestive Biscuits", "200g", "Grain"}, {"Unsalted Butter", "100g", "Dairy"}, {"Cream Cheese", "600g", "Dairy"}, {"Sugar", "150g", "Sweetener"}, {"Sour Cream", "200g", "Dairy"}, {"Eggs", "3", "Protein"}, {"Vanilla Extract", "1 tsp", "Spice"}, {"Lemon Zest", "1 tbsp", "Fruit"}, {"Lemon Juice", "2 tbsp", "Fruit"}},
                    //Tiramisu
                    {{"Ladyfingers", "24", "Grain"}, {"Mascarpone Cheese", "500g", "Dairy"}, {"Egg Yolks", "4", "Protein"}, {"Sugar", "100g", "Sweetener"}, {"Heavy Cream", "200ml", "Dairy"}, {"Espresso", "200ml", "Beverage"}, {"Cocoa Powder", "for dusting", "Spice"}, {"Dark Chocolate", "for grating", "Dairy"}, {"Rum or Marsala Wine", "2 tbsp", "Beverage"}},
                    //Apple Pie
                    {{"Apples", "6", "Fruit"}, {"Sugar", "150g", "Sweetener"}, {"Ground Cinnamon", "1 tsp", "Spice"}, {"Nutmeg", "1/4 tsp", "Spice"}, {"Lemon Juice", "1 tbsp", "Fruit"}, {"All-purpose Flour", "1 tbsp", "Grain"}, {"Butter", "50g", "Dairy"}, {"Pie Pastry", "2 sheets", "Grain"}, {"Egg", "1", "Protein"}, {"Vanilla Extract", "1 tsp", "Spice"}},
                    //Panna Cotta
                    {{"Heavy Cream", "500ml", "Dairy"}, {"Milk", "250ml", "Dairy"}, {"Sugar", "100g", "Sweetener"}, {"Vanilla Bean", "1", "Spice"}, {"Gelatin", "2 tsp", "Other"}, {"Water", "3 tbsp", "Liquid"}, {"Fresh Berries", "for topping", "Fruit"}}
            };
            //preparations for each meal
            String[][] preparations = {

                    //Main Courses
                    // Creamy Spinach Chicken
                    {"Melt butter in a pan over medium heat. Add garlic and cook until fragrant.",
                    "Add chicken breasts and cook until golden and cooked through.",
                    "Add spinach and cook until wilted.",
                    "Stir in double cream and let it simmer until thickened.",
                    "Grate Parmigiano Reggiano and stir into the sauce.",
                    "Serve hot, garnished with extra cheese if desired."},
                    // Chicken Chow Mein
                    {"Cook egg noodles according to package instructions and set aside.",
                    "Heat vegetable oil in a large pan. Add shallot and garlic and sauté until fragrant.",
                    "Add chicken breast and cook until browned and cooked through.",
                    "Add egg white and scramble until cooked.",
                    "Add cooked noodles and soy sauce, toss to coat.",
                    "Serve hot, garnished with extra soy sauce if desired."},
                    // Spaghetti Carbonara
                    {"Cook spaghetti according to package instructions and set aside.",
                    "In a large pan, cook guanciale until crispy.",
                    "Beat egg yolks and mix with grated Parmigiano Reggiano.",
                    "Add the cooked spaghetti to the pan with guanciale, tossing to coat.",
                    "Remove from heat and quickly toss with the egg mixture to create a creamy sauce.",
                    "Season with black pepper and salt to taste.",
                    "Serve immediately, garnished with extra cheese if desired."},
                    // Grilled Salmon
                    {"Preheat grill or grill pan to medium-high heat.",
                    "Rub salmon fillets with olive oil, lemon zest, minced garlic, salt, and black pepper.",
                    "Grill salmon for about 4-5 minutes on each side or until cooked through.",
                    "Serve with a squeeze of fresh lemon juice on top."},
                    // Beef Stroganoff
                    {"In a large pan, cook beef slices until browned and cooked through.",
                    "Remove beef and set aside. In the same pan, cook mushrooms and onion until softened.",
                    "Stir in sour cream and beef stock, bring to a simmer.",
                    "Add paprika and season with salt and pepper.",
                    "Return beef to the pan and simmer for 10 minutes.",
                    "Serve hot, optionally with mashed potatoes or pasta."},
                    // Vegetable Stir Fry
                    {"Heat sesame oil in a large pan or wok over medium-high heat.",
                    "Add ginger and cook for 1 minute until fragrant.",
                    "Add broccoli, carrot, and red pepper. Stir-fry until vegetables are tender-crisp.",
                    "Stir in soy sauce and cook for another 2-3 minutes.",
                    "Serve hot, optionally garnished with sesame seeds or fresh herbs."},

                    //Soups
                    // Thai-inspired Coconut Chicken Soup Preparation
                    {"Heat the oil in a large, deep saucepan over a medium-high heat. Cook the shallots, lemongrass, garlic, galangal, chillies, coriander stalks, and curry paste for 1-2 mins, stirring often until fragrant.",
                    "Pour in the coconut milk and stock, stirring well to release any browned bits on the bottom of the pan. Simmer for 10-15 mins.",
                    "Add the chicken pieces and lime leaves. Bring to a gentle bubble and cook for 10 mins until the chicken is cooked through. Add the mushrooms for the final 5 mins.",
                    "Stir in the fish sauce and sugar, then add lime juice to taste. Adjust with more fish sauce or sugar if needed.",
                    "Remove the lemongrass and galangal slices, then serve with fresh coriander leaves sprinkled on top."},
                    // Tomato Basil Soup Preparation
                    {"Heat olive oil in a large pot over medium heat. Add chopped onions and garlic, and sauté until softened, about 5 minutes.",
                    "Add chopped tomatoes (fresh or canned), vegetable broth, and a pinch of salt and pepper. Bring the mixture to a simmer.",
                    "Simmer for about 20-30 minutes, allowing the tomatoes to break down.",
                    "Once the soup has thickened, blend it with an immersion blender until smooth.",
                    "Stir in fresh basil leaves and continue to cook for another 5 minutes.",
                    "Serve with a drizzle of cream or olive oil and some fresh basil leaves for garnish."},
                    // Carrot Ginger Soup Preparation
                    {"Heat olive oil in a large pot over medium heat. Add chopped onions and garlic, sautéing until softened for about 5 minutes.",
                    "Add chopped carrots and fresh grated ginger, and cook for another 5 minutes to bring out the flavors.",
                    "Pour in vegetable broth and bring the soup to a boil. Once boiling, reduce to a simmer and cook for 20-30 minutes until the carrots are tender.",
                    "Blend the soup with an immersion blender until smooth.",
                    "Season with salt, pepper, and a pinch of cayenne pepper or nutmeg if desired. Serve hot."},
                    // Fish Soup Preparation
                    {"Heat oil in a large saucepan over medium heat. Cook onions for 5-7 minutes until softened.",
                    "Add garlic, potatoes, carrots, peppers, leeks, and tomato purée. Cook for 3-5 minutes.",
                    "Pour in the stock and add a bay leaf. Bring to a simmer, cover, and cook for 15 minutes.",
                    "Chop most of the dill and reserve a few whole sprigs. Season fish with salt, pepper, and lemon juice, then add to the soup along with the dill.",
                    "Simmer gently for 5-7 minutes until the fish is cooked through.",
                    "Remove the bay leaf and adjust seasoning to taste, adding more lemon juice if needed. Garnish with reserved dill sprigs."},
                    // Mushroom Soup Preparation
                    {"Heat olive oil or butter in a large pot over medium heat. Add sliced onions and garlic and sauté until softened, about 5 minutes.",
                    "Add sliced mushrooms and cook for another 10 minutes until they release their moisture and soften.",
                    "Pour in vegetable or chicken stock and bring the soup to a simmer. Let it cook for 15-20 minutes.",
                    "Blend the soup with an immersion blender for a smooth texture, or leave it chunky if you prefer.",
                    "Stir in a splash of cream and season with salt and pepper.",
                    "Garnish with fresh parsley or thyme before serving."},
                    // Lentil Soup Preparation
                    {"Heat olive oil in a large pot over medium heat. Add chopped onions, garlic, and carrots. Sauté until softened, about 5 minutes.",
                    "Add lentils, vegetable broth, and spices (such as cumin, coriander, and turmeric). Stir well.",
                    "Bring the mixture to a boil, then reduce to a simmer. Cook for 30-40 minutes until the lentils are tender.",
                    "Season with salt and pepper to taste, and blend part of the soup for a thicker texture, if desired.",
                    "Garnish with fresh cilantro or parsley before serving."},

                    //Salads
                    // Greek Salad Preparation
                    {"Place 4 large vine tomatoes, cut into wedges, 1 peeled, deseeded and chopped cucumber, ½ a thinly sliced red onion, 16 Kalamata olives, 1 tsp dried oregano, 85g feta cheese chunks, and 4 tbsp Greek extra virgin olive oil in a large bowl.",
                    "Lightly season the salad with salt and pepper.",
                    "Toss everything together gently and serve with crusty bread to mop up all of the juices."},
                    // Broccoli Salad Preparation
                    {"To make the pickle, heat the vinegar, sugar, and salt in a small pan. Boil for 1 min until the sugar dissolves, then add the red onion and simmer for 1 min.",
                    "Take off the heat, cover, and leave the pickling liquid to cool completely.",
                    "Mix the broccoli florets, carrots, cranberries, and cashews in a large bowl.",
                    "Add the cooled pickled onion, reserving the pickling liquid.",
                    "Whisk together the pickling liquid, oil, maple syrup, and lemon zest and juice with 2 tbsp cold water.",
                    "Pour the dressing over the salad and mix until well-coated."},
                    // Caesar Salad Preparation
                    {"Tear romaine lettuce into bite-sized pieces and place it in a large salad bowl.",
                    "Add croutons and a generous amount of freshly grated Parmesan cheese.",
                    "In a separate bowl, whisk together 2 tbsp olive oil, 1 tbsp Dijon mustard, 1 tbsp lemon juice, 2 tbsp Worcestershire sauce, 1 minced garlic clove, and salt and pepper to taste.",
                    "Toss the lettuce, croutons, and Parmesan with the Caesar dressing.",
                    "Garnish with more Parmesan cheese and black pepper, then serve immediately."},
                    // Fruit Salad Preparation
                    {"Chop a selection of seasonal fruits like strawberries, blueberries, mango, kiwi, and grapes into bite-sized pieces.",
                    "Add any citrus fruits, such as orange or pineapple, for added flavor.",
                    "Gently toss the fruit in a large mixing bowl.",
                    "Optionally, drizzle with a little honey or a splash of lime juice for extra sweetness and freshness.",
                    "Garnish with fresh mint leaves before serving."},
                    // Cobb Salad Preparation
                    {"Cook 2 chicken breasts, then dice them into bite-sized pieces.",
                    "Hard boil 4 eggs, peel, and chop them.",
                    "Prepare the lettuce by chopping into bite-sized pieces and placing it in a large bowl.",
                    "Add the chicken, eggs, 1 diced avocado, crumbled blue cheese, and cooked bacon bits to the salad.",
                    "Drizzle with your favorite dressing, such as ranch or vinaigrette.",
                    "Toss gently to combine, and serve immediately."},
                    // Quinoa Salad Preparation
                    {"Rinse 1 cup quinoa under cold water, then cook it according to package instructions (usually 2 cups water to 1 cup quinoa).",
                    "Let the cooked quinoa cool to room temperature.",
                    "Dice 1 cucumber, 1 bell pepper, and 1/2 red onion.",
                    "Chop fresh parsley and mint to add flavor to the salad.",
                    "In a large bowl, combine the cooled quinoa, chopped vegetables, and herbs.",
                    "For the dressing, whisk together 3 tbsp olive oil, 2 tbsp lemon juice, 1 tsp honey, and salt and pepper to taste.",
                    "Pour the dressing over the salad and toss gently until well combined."},

                    //Desserts
                    // Chocolate Brownie Preparation
                    {"Cut 185g unsalted butter into small cubes and place in a medium bowl. Break 185g dark chocolate into small pieces and add to the bowl.",
                    "Fill a small saucepan with hot water and place the bowl over it, ensuring it doesn't touch the water. Heat gently until the butter and chocolate have melted, stirring occasionally.",
                    "Alternatively, place the bowl in the microwave and heat on high for 2 minutes. Allow the melted mixture to cool to room temperature.",
                    "Preheat the oven to 180C/160C fan/gas 4 and line a 20cm square tin with kitchen foil or non-stick baking parchment.",
                    "Sift 85g plain flour and 40g cocoa powder together into a medium bowl to remove any lumps.",
                    "Chop 50g white chocolate and 50g milk chocolate into chunks and set aside.",
                    "Beat 3 large eggs and 275g golden caster sugar using an electric mixer on maximum speed until thick and creamy.",
                    "Gently fold the cooled chocolate mixture into the egg mixture, using a figure-eight motion to combine without losing air.",
                    "Sift the cocoa and flour mixture into the batter and fold it in gently.",
                    "Stir in the white and milk chocolate chunks.",
                    "Pour the mixture into the prepared tin and level the surface.",
                    "Bake for 25 minutes, checking the consistency by gently shaking the tin. If it wobbles in the middle, bake for another 5 minutes.",
                    "Allow the brownie to cool completely in the tin before cutting it into squares and serving."},
                    // Cheesecake Preparation
                    {"Preheat the oven to 180C/160C fan/gas 4 and line the base of a 23cm springform cake tin with parchment paper or foil.",
                    "Melt 85g butter in a medium pan and stir in 140g digestive biscuit crumbs and 1 tbsp golden caster sugar.",
                    "Press the mixture into the bottom of the tin and bake for 10 minutes. Cool on a wire rack.",
                    "In a stand mixer, beat 900g full-fat soft cheese at medium-low speed until creamy, about 2 minutes.",
                    "Gradually add 250g golden caster sugar, 3 tbsp plain flour, and a pinch of salt, scraping down the sides of the bowl.",
                    "Add 1½ tsp vanilla extract, 2 tsp lemon zest, and 1½ tsp lemon juice, then whisk in 3 large eggs and 1 yolk, one at a time.",
                    "Stir in 200ml soured cream and blend on low speed.",
                    "Brush the sides of the tin with melted butter and pour the filling into the prepared tin.",
                    "Bake for 10 minutes at 220C/200C fan/gas 7, then reduce the temperature to 110C/90C fan/gas ¼ and bake for 45 minutes.",
                    "Turn off the oven and leave the cheesecake to cool in the oven for 2 hours.",
                    "Combine the remaining soured cream with 1 tbsp sugar and 2 tsp lemon juice, and spread over the cooled cheesecake.",
                    "Refrigerate the cheesecake for at least 8 hours or overnight before serving."},

                    // Tiramisu Preparation
                    {"Brew 2 cups of strong coffee and let it cool to room temperature.",
                    "In a large bowl, whisk together 500g mascarpone cheese, 200ml heavy cream, 2 tbsp sugar, and 1 tsp vanilla extract.",
                    "In a separate bowl, whisk 4 large egg yolks and 4 tbsp sugar until thick and pale.",
                    "Gently fold the egg mixture into the mascarpone mixture, ensuring it is smooth and well combined.",
                    "Dip each ladyfinger into the cooled coffee and layer them in a serving dish.",
                    "Spread a layer of the mascarpone mixture over the ladyfingers, smoothing it with a spatula.",
                    "Repeat with another layer of dipped ladyfingers and mascarpone mixture.",
                    "Cover and refrigerate for at least 4 hours or overnight.",
                    "Before serving, dust the top with cocoa powder and garnish with chocolate shavings."},

                    // Apple Pie Preparation
                    {"Preheat the oven to 180C/160C fan/gas 4 and line a pie dish with pastry.",
                    "Peel, core, and slice 6 large apples and place them in a large bowl.",
                    "Mix the apples with 100g sugar, 1 tsp ground cinnamon, 1 tbsp lemon juice, and 2 tbsp flour.",
                    "Pour the apple mixture into the prepared pie crust.",
                    "Roll out a second sheet of pastry and place it over the apples. Trim and crimp the edges.",
                    "Cut slits in the top crust to allow steam to escape.",
                    "Bake the pie for 45-50 minutes until the crust is golden and the apples are tender.",
                    "Allow the pie to cool before serving."},

                    // Panna Cotta Preparation
                    {"In a small saucepan, heat 300ml heavy cream, 1 tsp vanilla extract, and 50g sugar until just simmering.",
                    "In a separate bowl, dissolve 2 tsp gelatin powder in 3 tbsp cold water and let it sit for 5 minutes.",
                    "Stir the gelatin into the hot cream mixture until fully dissolved.",
                    "Pour the mixture into individual serving glasses and refrigerate for at least 4 hours, or until set.",
                    "Before serving, top the panna cotta with fresh berries or a fruit compote."}
            };

            //loop over each meal
            for (int i = 0; i < meals.length; i++) {
                ContentValues mealValues = new ContentValues();
                mealValues.put("MealName", (String) meals[i][0]);
                mealValues.put("Photo", (Integer) meals[i][1]);
                mealValues.put("Type", (String) meals[i][2]);
                mealValues.putNull("UserID"); //set the userID null as this meal is not inserted by the user

                long mealId = db.insert("Meal", null, mealValues); //insert meal into Meal table and get MealID

                if (mealId != -1) {
                    //insert ingredients for this meal
                    for (String[] ingredient : ingredients[i]) {
                        ContentValues ingredientValues = new ContentValues();
                        ingredientValues.put("IngredientName", ingredient[0]);
                        ingredientValues.put("Amount", ingredient[1]);
                        ingredientValues.put("Type", ingredient[2]);
                        ingredientValues.put("MealID", mealId);
                        db.insert("Ingredient", null, ingredientValues);
                    }

                    //insert preparation steps for this meal
                    for (String step : preparations[i]) {
                        ContentValues preparationValues = new ContentValues();
                        preparationValues.put("PreparationDetail", step);
                        preparationValues.put("MealID", mealId);
                        db.insert("Preparation", null, preparationValues);
                    }
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
