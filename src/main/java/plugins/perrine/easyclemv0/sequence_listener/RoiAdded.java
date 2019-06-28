package plugins.perrine.easyclemv0.sequence_listener;

import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.WorkspaceState;
import plugins.perrine.easyclemv0.roi.RoiUpdater;

import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiAdded implements SequenceListener {

    private Sequence sequence;
    private WorkspaceState workspaceState;

    public RoiAdded(Sequence sequence, WorkspaceState workspaceState) {
        this.sequence = sequence;
        this.workspaceState = workspaceState;
    }

    @Override
    public void sequenceChanged(SequenceEvent event) {
        if (
            event.getSourceType() != SequenceEvent.SequenceEventSourceType.SEQUENCE_ROI ||
                event.getType() != SequenceEvent.SequenceEventType.ADDED
        ) {
            return;
        }

        if (workspaceState.isStopFlag()) {
            return;
        }

        workspaceState.setFlagReadyToMove(false);
        double z = event.getSequence().getFirstViewer().getPositionZ();

        ROI roi = (ROI) event.getSource();
        Point5D pos = roi.getPosition5D();
        pos.setZ(z);
        roi.setPosition5D(pos);

        roi.setColor(Colortab[(event.getSequence().getROICount(ROI.class) - 1) % Colortab.length]);
        roi.setName("Point " + event.getSequence().getROIs().size());
        roi.setStroke(6);

        ROI roisource = roi.getCopy();
        if (sequence == null) {
            new AnnounceFrame("You've closed the source image");
            return;
        }
        int zs = sequence.getFirstViewer().getPositionZ(); // was
        Point5D pos2 = roisource.getPosition5D();
        pos2.setZ(zs);
        roisource.setPosition5D(pos2);
        if ((sequence.getWidth() != event.getSequence().getWidth()) || (sequence.getHeight() != event.getSequence().getHeight())) {
            Point5D position = (Point5D) pos.clone();
            position.setLocation(sequence.getWidth() / 2, sequence.getHeight() / 2,
                sequence.getFirstViewer().getPositionZ(),
                sequence.getFirstViewer().getPositionT(), pos.getC());
            roisource.setPosition5D(position);

        }
        System.out.println("Adding Roi Landmark " + event.getSequence().getROICount(ROI.class) + " on source");
        roisource.setColor(roi.getColor());
        roisource.setName(roi.getName());
        roisource.setStroke(roi.getStroke());
        roisource.setFocused(false);
        sequence.addROI(roisource);
        workspaceState.setFlagReadyToMove(true);
        workspaceState.setDone(false);
    }

    @Override
    public void sequenceClosed(Sequence sequence) {

    }
}
