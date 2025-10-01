import {useMemo} from "react";

export default function AppPresentation() {
    const DAYS = ["Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"] as const;

    const W = 1000, H = 480;
    const gridX = 100, gridY = 160, gridW = 720, gridH = 220;
    const colW = gridW / DAYS.length;
    const dayY = 120;
    const centers = useMemo(() => DAYS.map((_, i) => gridX + colW * (i + 0.5)), [colW]);
    const timeToY = (h: number, m = 0) => gridY + (((h + m / 60) - 8) / 14) * gridH;

    const availBands = [
        {label: "Disponibilités matin", start: timeToY(9), end: timeToY(12), color: "var(--ms-primary)"},
        {label: "Disponibilités après-midi", start: timeToY(14), end: timeToY(16), color: "var(--ms-secondary)"},
    ];

    const chips = [
        {day: 1, y: timeToY(9, 0), w: 136, label: "Tâche planifiée", color: "var(--ms-primary)"},
        {day: 3, y: timeToY(14, 0), w: 120, label: "Rendez-vous", color: "var(--ms-secondary)"},
        {day: 4, y: timeToY(18, 0), w: 128, label: "Pause", color: "var(--ms-peach)"},
    ];

    const callouts = [
        {text: "Définis tes disponibilités.", ax: gridX + 16, ay: timeToY(9) + 6, x: 60, y: 46, w: 360, h: 44},
        {text: "Choisis tes objectifs.", ax: gridX + colW * 3 + 18, ay: timeToY(14) + 8, x: 560, y: 50, w: 360, h: 44},
        {
            text: "Priorités de la semaine : visibles en un coup d’œil.",
            ax: gridX + gridW - 40,
            ay: gridY + gridH - 18,
            x: 560,
            y: 420,
            w: 360,
            h: 44
        },
    ];

    return (
        <section aria-label="Présentation My Sheïla — Planning compréhensible"
                 className="relative h-full overflow-hidden">
            <div aria-hidden className="absolute inset-0" style={{
                background: `
          radial-gradient(1200px 800px at -20% -20%, color-mix(in oklab, var(--ms-primary) 22%, transparent) 0%, transparent 60%),
          radial-gradient(900px 700px at 110% 10%, color-mix(in oklab, var(--ms-secondary) 18%, transparent) 0%, transparent 70%),
          radial-gradient(700px 600px at 50% 100%, color-mix(in oklab, var(--ms-peach) 14%, transparent) 0%, transparent 75%),
          linear-gradient(180deg, #ffffff 0%, #faf7ff 100%)
        `,
            }}/>
            <div aria-hidden className="pointer-events-none absolute inset-0 opacity-[0.04]"
                 style={{
                     backgroundImage: "radial-gradient(#000 0.6px, transparent 0.6px)",
                     backgroundSize: "14px 14px"
                 }}/>

            <div className="relative px-6 pt-8">
                <h2 className="mt-3 text-xl font-semibold text-[color:var(--ms-ink)]">
                    MySheïla transforme tes routines en un planning clair et motivant — sans friction, sans bruit.
                </h2>
                <p className="mt-1 text-sm text-[color:var(--ms-ink-soft)]">Planifie moins. Vis plus.</p>
            </div>

            <svg viewBox={`0 0 ${W} ${H}`} className="relative block h-auto w-full">
                <defs>
                    <filter id="soft" x="-50%" y="-50%" width="200%" height="200%">
                        <feGaussianBlur in="SourceAlpha" stdDeviation="3"/>
                        <feOffset dx="0" dy="1.5"/>
                        <feComponentTransfer>
                            <feFuncA type="linear" slope="0.18"/>
                        </feComponentTransfer>
                        <feMerge>
                            <feMergeNode/>
                            <feMergeNode in="SourceGraphic"/>
                        </feMerge>
                    </filter>
                    <clipPath id="gridClip">
                        <rect x={gridX} y={gridY} width={gridW} height={gridH} rx="12"/>
                    </clipPath>
                    <linearGradient id="panel" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0" stopColor="#ffffff" stopOpacity="0.96"/>
                        <stop offset="1" stopColor="#ffffff" stopOpacity="0.90"/>
                    </linearGradient>
                </defs>

                <g filter="url(#soft)">
                    <rect x={gridX} y={gridY} width={gridW} height={gridH} rx="12" fill="url(#panel)"
                          stroke="color-mix(in oklab, var(--ms-primary) 18%, #E5E7EB)"/>
                    {Array.from({length: DAYS.length - 1}).map((_, i) => (
                        <line key={i} x1={gridX + colW * (i + 1)} y1={gridY} x2={gridX + colW * (i + 1)}
                              y2={gridY + gridH}
                              stroke="color-mix(in oklab, var(--ms-primary) 12%, #E5E7EB)"/>
                    ))}
                    {Array.from({length: 4}).map((_, i) => (
                        <line key={i} x1={gridX} y1={gridY + ((i + 1) * gridH) / 5} x2={gridX + gridW}
                              y2={gridY + ((i + 1) * gridH) / 5}
                              stroke="color-mix(in oklab, var(--ms-secondary) 10%, #E5E7EB)"/>
                    ))}
                </g>

                <g clipPath="url(#gridClip)">
                    {availBands.map((b, idx) => {
                        const y = b.start, h = Math.max(6, b.end - b.start);
                        return (
                            <g key={idx} opacity="0.9">
                                <rect x={gridX} y={y} width={gridW} height={h} rx={6} fill={b.color} opacity="0.10"/>
                                <rect x={gridX} y={y} width={gridW} height={1} fill={b.color} opacity="0.18"/>
                                <rect x={gridX} y={y + h - 1} width={gridW} height={1} fill={b.color} opacity="0.18"/>
                            </g>
                        );
                    })}
                </g>

                {centers.map((cx, i) => (
                    <text key={DAYS[i]} x={cx} y={dayY} textAnchor="middle" fontSize="12"
                          fill="var(--ms-ink-soft)" style={{fontWeight: 600}}>{DAYS[i]}</text>
                ))}

                <g clipPath="url(#gridClip)" filter="url(#soft)">
                    {chips.map((c, idx) => {
                        const x = gridX + colW * c.day + 12;
                        const h = 22;
                        return (
                            <g key={idx}>
                                <rect x={x - 6} y={c.y - 8} width={c.w + 12} height={h + 6} rx={12} fill={c.color}
                                      opacity="0.08"/>
                                <rect x={x} y={c.y} width={c.w} height={h} rx={11} fill="#fff" opacity="0.98"/>
                                <circle cx={x + 12} cy={c.y + 11} r={4} fill={c.color}/>
                                <text x={x + 26} y={c.y + 15} fontSize="11" fill="var(--ms-ink)"
                                      fontWeight="600">{c.label}</text>
                            </g>
                        );
                    })}
                </g>

                {callouts.map((c, i) => (
                    <g key={`co-${i}`}>
                        <line x1={c.x + c.w / 2} y1={c.y + c.h} x2={c.ax} y2={c.ay}
                              stroke="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)" strokeWidth="1.5"
                              strokeDasharray="4 6"/>
                        <circle cx={c.ax} cy={c.ay} r="3" fill="#fff"
                                stroke="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)" strokeWidth="1"/>
                        <rect x={c.x} y={c.y} width={c.w} height={c.h} rx="10" fill="#fff" opacity="0.98"/>
                        <text x={c.x + 12} y={c.y + 20} fontSize="11" fill="var(--ms-ink)">{c.text}</text>
                    </g>
                ))}

                <g>
                    <line x1={gridX - 28} y1={gridY + 8} x2={gridX - 28} y2={gridY + gridH - 8}
                          stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1.5"
                          strokeDasharray="4 6" opacity="0.75"/>
                    <circle cx={gridX - 28} cy={gridY + 8} r="3" fill="#fff"
                            stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1"/>
                    <circle cx={gridX - 28} cy={gridY + gridH - 8} r="3" fill="#fff"
                            stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1"/>
                    <text x={gridX - 62} y={gridY + 14} fontSize="11"
                          fill="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)">08:00
                    </text>
                    <text x={gridX - 62} y={gridY + gridH - 2} fontSize="11"
                          fill="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)">22:00
                    </text>
                </g>
            </svg>
        </section>
    );
}