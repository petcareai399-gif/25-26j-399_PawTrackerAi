"""
Lightweight integration helpers for consuming external epidemiological
and weather data sources to enrich outbreak risk predictions.

These functions intentionally avoid hard-coding any vendor-specific
credentials so that different deployments can supply their own
configuration at runtime.
"""

from dataclasses import dataclass
from typing import Any, Dict, List


@dataclass
class ExternalObservation:
    """Container for a single external signal used in risk assessment."""

    location: str
    temperature_c: float
    humidity_pct: float
    rainfall_mm: float
    source: str


def normalize_external_payload(payload: Dict[str, Any]) -> ExternalObservation:
    """
    Normalise an arbitrary JSON payload from an external API into the
    minimal set of features required by the viral outbreak model.
    """

    return ExternalObservation(
        location=str(payload.get("location") or payload.get("city") or "UNKNOWN"),
        temperature_c=float(payload.get("temperature_c", 0.0)),
        humidity_pct=float(payload.get("humidity_pct", 0.0)),
        rainfall_mm=float(payload.get("rainfall_mm", 0.0)),
        source=str(payload.get("source") or "unspecified"),
    )


def batch_from_api_response(items: List[Dict[str, Any]]) -> List[ExternalObservation]:
    """
    Convert a list of JSON-like dictionaries from a public API into a
    list of normalised `ExternalObservation` instances.
    """

    return [normalize_external_payload(item) for item in items]


