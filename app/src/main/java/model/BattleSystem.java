package model;

import java.util.List;

import presenter.GamePresenter;

/**
 * - presenter: GamePresenter
 * - enemyState: EnemyState
 * - playerState: PlayerState
 * - playerSkills: List<Skill>
 * - enemySkills: List<Skill>
 * - CurrentAction: enum
 */
public class BattleSystem {
    private GamePresenter presenter;
    private EnemyState enemyState;
    private List<Skill> playerSkill;
    private List<Skill> enemySkills;
    private enum CurrentAction{
        MOVE,
        ATTACK,
        USE
    }


}
