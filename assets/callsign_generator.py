#!/usr/bin/env python3
"""
MorseVerse Callsign Generator
Generates random amateur radio callsigns for practice
"""

import random
import json

# ITU prefix allocations (simplified)
PREFIXES = {
    "US": ["W", "K", "N", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AI", "AJ", "AK", "AL"],
    "Canada": ["VE", "VA", "VY"],
    "UK": ["G", "M", "2E"],
    "Germany": ["DL", "DJ", "DK", "DM", "DN"],
    "Italy": ["I", "IK", "IU", "IV"],
    "Japan": ["JA", "JH", "JI", "JJ", "JK", "JL", "JM", "JN", "JO", "JP"],
    "Australia": ["VK"],
    "New Zealand": ["ZL"],
    "South Africa": ["ZS"],
    "Brazil": ["PY"],
    "Argentina": ["LU"],
    "Chile": ["CE"],
    "Uruguay": ["CX"],
    "Russia": ["UA", "RA", "RK", "RN", "RV", "RW", "RX"],
    "Finland": ["OH"],
    "Sweden": ["SM"],
    "Norway": ["LA"],
    "Denmark": ["OZ"],
    "Belgium": ["ON"],
    "Netherlands": ["PA"],
    "Switzerland": ["HB9", "HB0"],
    "Croatia": ["9A"],
    "Slovenia": ["S5"],
    "Czech Republic": ["OK"],
    "Slovakia": ["OM"],
    "Poland": ["SP", "SQ"],
    "France": ["F"],
    "Spain": ["EA"],
    "Portugal": ["CT"],
    "Austria": ["OE"],
    "Hungary": ["HA"],
    "Romania": ["YO"],
    "Bulgaria": ["LZ"],
    "Greece": ["SV"],
    "Turkey": ["TA"],
    "Israel": ["4X"],
    "India": ["VU"],
    "China": ["BV", "BA"],
    "South Korea": ["HL"],
    "Taiwan": ["BV", "BU"],
    "Thailand": ["HS"],
    "Indonesia": ["YB"],
    "Malaysia": ["9M"],
    "Philippines": ["DU"],
    "Mexico": ["XE"],
    "Cuba": ["CO"],
    "Colombia": ["HK"],
    "Peru": ["OA"],
    "Ecuador": ["HC"],
    "Venezuela": ["YV"]
}

SUFFIX_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


def generate_callsign(country=None, count=1):
    """Generate random amateur radio callsigns"""
    callsigns = []

    for _ in range(count):
        if country:
            prefix = random.choice(PREFIXES.get(country, ["W"]))
        else:
            all_prefixes = [p for prefixes in PREFIXES.values() for p in prefixes]
            prefix = random.choice(all_prefixes)

        # Add optional digit (region number)
        digit = random.randint(0, 9)

        # Add suffix (1-3 characters)
        suffix_len = random.randint(1, 3)
        suffix = ''.join(random.choices(SUFFIX_CHARS, k=suffix_len))

        callsign = f"{prefix}{digit}{suffix}"
        callsigns.append(callsign)

    return callsigns


def generate_contest_exchange():
    """Generate a typical contest exchange"""
    rst = f"{random.randint(1,5)}{random.randint(1,9)}{random.randint(1,9)}"
    serial = random.randint(1, 9999)
    state = random.choice(["NY", "CA", "TX", "FL", "IL", "PA", "OH", "GA", "NC", "MI"])

    return {
        "rst": rst,
        "serial": f"{serial:04d}",
        "state": state
    }


if __name__ == "__main__":
    # Generate sample callsigns
    print("Sample Callsigns:")
    print("-" * 40)

    for country in ["US", "Canada", "UK", "Germany", "Japan", "Australia"]:
        callsigns = generate_callsign(country, 3)
        print(f"{country}: {', '.join(callsigns)}")

    print("\nSample Contest Exchange:")
    print("-" * 40)
    for _ in range(5):
        exchange = generate_contest_exchange()
        print(f"RST: {exchange['rst']}, Serial: {exchange['serial']}, State: {exchange['state']}")

    # Generate JSON file with callsigns for practice
    practice_callsigns = []
    for _ in range(100):
        callsign = generate_callsign()
        practice_callsigns.append({
            "callsign": callsign[0],
            "morse": " ".join([char_to_morse(c) for c in callsign[0]])
        })

    with open("practice_callsigns.json", "w") as f:
        json.dump(practice_callsigns, f, indent=2)

    print(f"\nGenerated {len(practice_callsigns)} practice callsigns")
