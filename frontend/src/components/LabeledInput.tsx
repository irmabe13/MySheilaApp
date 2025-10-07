import {type ComponentProps} from "react";

export default function LabeledInput(
    {label, className = "", ...props}: { label: string } & ComponentProps<"input">
) {
    return (
        <label className="block">
            <span className="mb-1 block text-xs font-medium text-gray-700">{label}</span>
            <input
                {...props}
                className={[
                    "w-full rounded-2xl border border-gray-200 bg-white px-3 py-2 shadow-sm",
                    "focus:border-[color:var(--ms-primary)] focus:outline-none focus:ring-2 focus:ring-[color:var(--ms-primary)]/20",
                    className,
                ].join(" ")}
            />
        </label>
    );
}