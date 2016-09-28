package com.example.administrator.duchess;

/**
 * Created by Administrator on 2016-8-22.
 */
public class Player {
    public enum Phase{Start, PieceSelected, PiecePlaced,End};
    /*when in player's phase, 'phase enter starting phase.
    when a piece is selected, player enter pieceSelected phase, piece's background colored in blue
    and color all valid movement place to red.
    when a piece is selected, player enter piecePlace phase, piece's transparent in the original
    place and appear in the new position with transparent and colored the location
    */
    Phase phase = Phase.End;
}
