from pathlib import Path
import csv
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]
CSV_PATH = ROOT / "results" / "results.csv"
PLOTS_DIR = ROOT / "results" / "plots"
PLOTS_DIR.mkdir(parents=True, exist_ok=True)

rows = []
with CSV_PATH.open(newline="", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        row["n"] = int(row["n"])
        row["time_ms"] = float(row["time_ms"])
        row["steps"] = int(row["steps"])
        row["moves"] = int(row["moves"])
        row["comparisons"] = int(row["comparisons"])
        rows.append(row)

def series_for(workload, metric):
    filtered = [r for r in rows if r["workload"] == workload]
    groups = {}
    for r in filtered:
        label = f'{r["structure"]}/{r["variant"]}' if workload == "W3" else r["structure"]
        groups.setdefault(label, []).append((r["n"], r[metric]))
    for label in groups:
        groups[label].sort()
    return groups

def save_time_chart(workload, title):
    fig, ax = plt.subplots(figsize=(8.2, 4.8))
    for label, values in series_for(workload, "time_ms").items():
        ax.plot([n for n, _ in values], [v for _, v in values], marker="o", label=label)
    ax.set_xscale("log")
    ax.set_xlabel("n")
    ax.set_ylabel("Median time (ms)")
    ax.set_title(title)
    ax.grid(True, alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(PLOTS_DIR / f"{workload.lower()}_time.png", dpi=170)
    plt.close(fig)

def save_operations_chart(workload, title):
    fig, ax = plt.subplots(figsize=(9.4, 5.4))
    metric_markers = {"steps": "o", "moves": "s", "comparisons": "^"}
    for metric, marker in metric_markers.items():
        for label, values in series_for(workload, metric).items():
            ax.plot(
                [n for n, _ in values],
                [v for _, v in values],
                marker=marker,
                label=f"{label} {metric}",
            )
    ax.set_xscale("log")
    ax.set_yscale("symlog", linthresh=1)
    ax.set_xlabel("n")
    ax.set_ylabel("Operation count")
    ax.set_title(title)
    ax.grid(True, alpha=0.3)
    ax.legend(fontsize=8)
    fig.tight_layout()
    fig.savefig(PLOTS_DIR / f"{workload.lower()}_operations.png", dpi=170)
    plt.close(fig)

titles = {
    "W1": ("W1 Random Access — Time vs n", "W1 Random Access — Physical Operations vs n"),
    "W2": ("W2 Search — Time vs n", "W2 Search — Physical Operations vs n"),
    "W3": ("W3 Insert & Remove — Time vs n", "W3 Insert & Remove — Physical Operations vs n"),
    "W4": ("W4 Priority Processing — Time vs n", "W4 Priority Processing — Physical Operations vs n"),
}

for workload, (time_title, ops_title) in titles.items():
    save_time_chart(workload, time_title)
    save_operations_chart(workload, ops_title)

print(f"Plots written to {PLOTS_DIR}")
