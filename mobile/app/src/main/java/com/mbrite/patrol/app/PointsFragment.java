package com.mbrite.patrol.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbrite.patrol.common.AudioMgr;
import com.mbrite.patrol.common.Constants;
import com.mbrite.patrol.common.FileMgr;
import com.mbrite.patrol.common.Tracker;
import com.mbrite.patrol.common.Utils;
import com.mbrite.patrol.content.providers.RecordProvider;
import com.mbrite.patrol.model.PointGroup;
import com.mbrite.patrol.model.PointRecord;
import com.mbrite.patrol.model.RecordStatus;

import java.util.UUID;

public class PointsFragment extends Fragment {
    private static final String TAG = PointsFragment.class.getSimpleName();
    private static final int THUMB_SIZE = 256;
    protected PointGroup point;
    protected PointRecord pointRecord;
    protected View view;
    protected String message;
    protected EditText memoView;
    protected InputMethodManager imm;
    protected int[] layoutIds = new int[]{
            R.id.title,
            R.id.pointCodeLine,
//            R.id.secondLine,
            R.id.range,
            R.id.select_content,
            R.id.content,
            R.id.memo,
            R.id.imageLine,
            R.id.recordLine
    };
    protected String value = "";
    protected int status = RecordStatus.PASS; // Default to Pass
    private TextView recordBtn;
    private TextView playRecordingBtn;
    private TextView deleteRecordingBtn;
    private AudioMgr audioMgr = new AudioMgr();

    protected View renderView(LayoutInflater inflater, int resource) {
        int pointId = Integer.parseInt(this.getTag());
        point = Tracker.INSTANCE.getPointDuplicates().get(pointId).get(0);
        pointRecord = RecordProvider.INSTANCE.getPointRecord(pointId);
        view = inflater.inflate(resource, null);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            TextView nameView = (TextView) view.findViewById(R.id.name);
            nameView.setText(point.name);
            TextView descriptionView = (TextView) view.findViewById(R.id.description);
            descriptionView.setText(point.description);
            if (point.pointCode != null) {
                ((TextView) view.findViewById(R.id.pointCode)).setText(point.pointCode);
            }
//            TextView stateView = (TextView) view.findViewById(R.id.state);
//            stateView.setText(point.state);
            memoView = (EditText) view.findViewById(R.id.memo_value);
            if (pointRecord != null) {
                memoView.setText(pointRecord.memo);
            }

            if (Constants.CATEGORY_SHOW_GRAPH.contains(point.category)) {
                TextView showGraphBtn = (TextView) view.findViewById(R.id.show_graph_btn);
                showGraphBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Tracker.INSTANCE.targetPoint = point;
                        Intent intent = new Intent(getActivity(), HistoricalDataGraphActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                view.findViewById(R.id.show_graph).setVisibility(View.GONE);
            }

            setupAddPhotoButton(view);
            setupRecordButton(view);
            setupPlayButton(view);
            setupDeleteRecordingBtn(view);
            setBackground();
        } catch (Exception ex) {
            Utils.showErrorPopupWindow(getActivity(), ex);
        }
        return view;
    }

    /**
     * @return true if successful, false if error occurred
     */
    public boolean save() {
        try {
            RecordProvider.INSTANCE.addOrUpdatePointRecord(point, value, status,
                    memoView.getText().toString(), getActivity());
            return true;
        } catch (Exception ex) {
            message = String.format(getString(R.string.error_of), ex.getLocalizedMessage());
        }

        return false;
    }

    /**
     * @return true if entered value is valid, false otherwise
     */
    public boolean validate() {
        return true;
    }

    public String getWarning() {
        return null;
    }

    private void setupAddPhotoButton(View view) {
        ImageView image = (ImageView) view.findViewById(R.id.image);
        Button button = (Button) view.findViewById(R.id.add_memo_photo);
        if (point.getImage() != null ||
                (pointRecord != null && pointRecord.image != null)) {
            if (point.getImage() == null) {
                point.setImage(pointRecord.image);
            }
            button.setBackground(getResources().getDrawable(R.drawable.background_green));
            button.setText(R.string.change_photo);
            String imagePath = FileMgr.getFullPath(getActivity(), point.getImage());
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(imagePath), THUMB_SIZE, THUMB_SIZE);
            image.setImageBitmap(bitmap);
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.background_cyan));
            button.setText(R.string.add_photo);
            image.setImageBitmap(null);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PointsActivity) getActivity()).save(false);
                Tracker.INSTANCE.targetPoint = point;
                Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void setupRecordButton(View view) {
        recordBtn = (TextView) view.findViewById(R.id.record);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    return;
                }
                if (isRecording()) {
                    recordBtn.setText(R.string.record_voice);
                    recordBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                            getResources().getDrawable(R.drawable.ic_btn_speak_now),
                            null,
                            null);

                    audioMgr.stopRecording();
                    playRecordingBtn.setBackground(getResources().getDrawable(R.drawable.background_green));
                } else {
                    recordBtn.setText(R.string.stop);
                    recordBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                            getResources().getDrawable(R.drawable.stop_icon),
                            null,
                            null);

                    try {
                        if (point.getAudio() != null) {
                            FileMgr.delete(getActivity(), point.getAudio());
                        }
                        point.setAudio(UUID.randomUUID() + Constants.AUDIO_FILE_SUFFIX);
                        audioMgr.startRecording(
                                FileMgr.getFullPath(getActivity(), point.getAudio()));
                    } catch (Exception ex) {
                        Utils.showErrorPopupWindow(getActivity(), ex);
                    }
                }
            }
        };
        recordBtn.setOnClickListener(clickListener);
    }

    private void setupPlayButton(View view) {
        playRecordingBtn = (TextView) view.findViewById(R.id.play);
        if (point.getAudio() != null ||
                (pointRecord != null && pointRecord.audio != null)) {
            if (point.getAudio() == null) {
                point.setAudio(pointRecord.audio);
            }
            playRecordingBtn.setBackground(getResources().getDrawable(R.drawable.background_green));
        } else {
            playRecordingBtn.setBackground(getResources().getDrawable(R.drawable.background_cyan));
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording()) {
                    return;
                }
                if (isPlaying()) {
                    setPlayButtonOff();
                    audioMgr.stopPlaying();
                } else {
                    if (point.getAudio() == null) {
                        new AlertDialog.Builder(getActivity(),
                                R.style.Theme_Base_AppCompat_Dialog_FixedSize)
                                .setMessage(R.string.no_recording)
                                .setTitle(R.string._notice)
                                .setCancelable(false)
                                .setPositiveButton(R.string.confirm,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // do nothing
                                            }
                                        }
                                ).setIcon(android.R.drawable.ic_dialog_info).show();
                        return;
                    }

                    playRecordingBtn.setText(R.string.stop);
                    playRecordingBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                            getResources().getDrawable(R.drawable.stop_icon),
                            null,
                            null);

                    try {
                        MediaPlayer.OnCompletionListener listener =
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        setPlayButtonOff();
                                    }
                                };
                        audioMgr.startPlaying(
                                FileMgr.getFullPath(getActivity(), point.getAudio()), listener);
                    } catch (Exception ex) {
                        Utils.showErrorPopupWindow(getActivity(), ex);
                    }
                }
            }
        };
        playRecordingBtn.setOnClickListener(clickListener);
    }

    private void setupDeleteRecordingBtn(View view) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording() || isPlaying()) {
                    return;
                }
                try {
                    playRecordingBtn.setBackground(getResources().getDrawable(R.drawable.background_cyan));

                    if (point.getAudio() != null) {
                        FileMgr.delete(getActivity(), point.getAudio());
                        RecordProvider.INSTANCE.removePointRecordAudio(
                                getActivity(), point.id);
                        point.setAudio(null);

                        Toast.makeText(
                                getActivity(),
                                R.string.delete_recording_success,
                                Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(
                                getActivity(),
                                R.string.no_recording,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception ex) {
                    Utils.showErrorPopupWindow(getActivity(), ex);
                }
            }
        };
        deleteRecordingBtn = (TextView) view.findViewById(R.id.delete_recording);
        deleteRecordingBtn.setOnClickListener(clickListener);
    }

    private boolean isRecording() {
        return getActivity().getString(R.string.stop).equals(recordBtn.getText());
    }

    private boolean isPlaying() {
        return getActivity().getString(R.string.stop).equals(playRecordingBtn.getText());
    }

    private void setPlayButtonOff() {
        playRecordingBtn.setText(R.string.play);
        playRecordingBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.ic_media_play),
                null,
                null);
    }

    private void setBackground() {
        if (pointRecord != null) {
            // set background color
            int resId;
            switch (pointRecord.status) {
                case RecordStatus.PASS:
                    // pass
                    resId = R.drawable.pass_row_selector;
                    break;
                case RecordStatus.FAIL:
                    // fail
                    resId = R.drawable.fail_row_selector;
                    break;
                case RecordStatus.WARN:
                    resId = R.drawable.warning_row_selector;
                    // warning
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid point status: %d", pointRecord.status));
            }

            for (int id : layoutIds) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.setBackgroundResource(resId);
                }
            }
        }
    }

}
