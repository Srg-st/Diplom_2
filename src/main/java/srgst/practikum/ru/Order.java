package srgst.practikum.ru;

import java.util.ArrayList;


public class Order {
    private ArrayList<String> ingredients;
    private String accessToken;

    public Order(ArrayList<String> ingredients, String accessToken) {
            this.ingredients = ingredients;
            this.accessToken = accessToken;
    }

    public Order() {

    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
