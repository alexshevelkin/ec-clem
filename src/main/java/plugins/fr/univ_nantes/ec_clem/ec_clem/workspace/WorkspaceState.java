/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.fr.univ_nantes.ec_clem.ec_clem.workspace;

import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;

public class WorkspaceState {

    private PointType pointType;
    private boolean flagReadyToMove;
    private boolean done;

    public WorkspaceState(boolean flagReadyToMove, boolean done, PointType pointType) {
        this.flagReadyToMove = flagReadyToMove;
        this.done = done;
        this.pointType = pointType;
    }

    public boolean isFlagReadyToMove() {
        return flagReadyToMove;
    }

    public void setFlagReadyToMove(boolean flagReadyToMove) {
        this.flagReadyToMove = flagReadyToMove;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }
}