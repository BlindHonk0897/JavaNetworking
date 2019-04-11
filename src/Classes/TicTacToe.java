/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.ArrayList;

/**
 *
 * @author alingasada_sd2021
 */
public class TicTacToe {
    
    private ArrayList<Integer> availList;
    
    public TicTacToe() {
        this.availList = new ArrayList();
        for(int i = 0;i<9;i++){
            availList.add(i+1);
        }
    }

    public ArrayList<Integer> getAvailList() {
        return availList;
    }
    
    
    
}
