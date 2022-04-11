package com.example.namkeep.ui.home.Helper;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void inItemDismiss(int position);
}
