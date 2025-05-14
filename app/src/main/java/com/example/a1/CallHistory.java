package com.example.a1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CallHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        RecyclerView recyclerView = findViewById(R.id.rv_full_call_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get full call history (replace with actual data source)
        List<CallHistoryItem> callHistory = getFullCallHistory();

        FullCallHistoryAdapter adapter = new FullCallHistoryAdapter(callHistory);
        recyclerView.setAdapter(adapter);
    }

    private List<CallHistoryItem> getFullCallHistory() {
        List<CallHistoryItem> items = new ArrayList<>();
        // Add sample data with call types (replace with actual data)
        items.add(new CallHistoryItem(
                "+1 (555) 123-4567",
                "5 min 23 sec",
                "Today, 10:30 AM",
                CallHistoryItem.CallType.OUTGOING,
                System.currentTimeMillis(),
                false
        ));
        items.add(new CallHistoryItem(
                "+1 (555) 987-6543",
                "2 min 45 sec",
                "Yesterday, 4:15 PM",
                CallHistoryItem.CallType.INCOMING,
                System.currentTimeMillis() - 86400000,
                true
        ));
        items.add(new CallHistoryItem(
                "+1 (555) 456-7890",
                "--:--",
                "Monday, 2:00 PM",
                CallHistoryItem.CallType.MISSED,
                System.currentTimeMillis() - 172800000,
                false
        ));
        return items;
    }

    public static class CallHistoryItem {
        public enum CallType {
            OUTGOING,
            INCOMING,
            MISSED,
            REJECTED,
            BLOCKED,
            UNKNOWN
        }

        public String number;
        public String duration;
        public String time;
        public CallType callType;
        public long timestamp;
        public boolean isVideoCall;

        public CallHistoryItem(String number, String duration, String time,
                               CallType callType, long timestamp, boolean isVideoCall) {
            this.number = number;
            this.duration = duration;
            this.time = time;
            this.callType = callType;
            this.timestamp = timestamp;
            this.isVideoCall = isVideoCall;
        }
    }

    private static class FullCallHistoryAdapter extends RecyclerView.Adapter<FullCallHistoryAdapter.ViewHolder> {
        private final List<CallHistoryItem> items;

        public FullCallHistoryAdapter(List<CallHistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call_history_full, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallHistoryItem item = items.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvNumber, tvDuration, tvTime, tvCallType;
            private final ImageView ivCallType, ivVideoCall;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tv_number);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvCallType = itemView.findViewById(R.id.tv_call_type);
                ivCallType = itemView.findViewById(R.id.iv_call_type);
                ivVideoCall = itemView.findViewById(R.id.iv_video_call);
            }

            public void bind(CallHistoryItem item, int position) {
                tvNumber.setText(item.number);
                tvDuration.setText(item.duration);
                tvTime.setText(item.time);

                // Set call type based on actual data
                if (item.callType != null) {
                    switch (item.callType) {
                        case OUTGOING:
                            ivCallType.setImageResource(R.drawable.ic_call_made);
                            tvCallType.setText("Outgoing");
                            break;
                        case INCOMING:
                            ivCallType.setImageResource(R.drawable.ic_call_received);
                            tvCallType.setText("Incoming");
                            break;
                        case MISSED:
                            ivCallType.setImageResource(R.drawable.ic_call_missed);
                            tvCallType.setText("Missed");
                            tvCallType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorError));
                            break;
                        case REJECTED:
                            ivCallType.setImageResource(R.drawable.ic_call_rejected);
                            tvCallType.setText("Rejected");
                            tvCallType.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorError));
                            break;
                        default:
                            ivCallType.setImageResource(R.drawable.ic_call);
                            tvCallType.setText("Call");
                    }
                } else {
                    // Fallback logic if callType is not available
                    boolean isOutgoing = position % 2 == 0;
                    ivCallType.setImageResource(isOutgoing ?
                            R.drawable.ic_call_made : R.drawable.ic_call_received);
                    tvCallType.setText(isOutgoing ? "Outgoing" : "Incoming");
                }

                // Show video call icon if applicable
                ivVideoCall.setVisibility(item.isVideoCall ? View.VISIBLE : View.GONE);
            }
        }
    }
}