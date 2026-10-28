# Onboard

Key: Figma node `1:2`
Screenshots: [default](onboard.png)
Covers: AC-1, AC-10

## Layout
```
┌─────────────────────────┐
│                         │
│   full-bleed artwork    │  img_onboarding_bg, fills the screen
│                         │
│                         │
│ THE MOVIE DB            │  img_logo_tmdb
│ Tudo sobre filmes,      │  headline, 3 lines
│ séries, animes e        │
│ muito mais.             │
│ Fique por dentro das …  │  body, 2 lines
│ ┌─────────────────────┐ │
│ │       Acessar       │ │  primary button
│ └─────────────────────┘ │
└─────────────────────────┘
```
Content block is 315 pt wide with 30 pt side margins, anchored to the bottom: logo, 19 pt to headline,
19 pt to body, 19 pt to button, 31 pt from the button to the bottom edge.

## States
| State | What is shown | Actions |
|---|---|---|
| Default | Artwork, logo, headline, body, button | Tap "Acessar" → home (removed from back stack) |

## Components
- Background: image fills 375 × 812; over it a gradient from #15151D at 85% opacity (from 37% height) to
  transparent (at 69%), darkening the lower half, with a background blur.
- Logo: 176 × 15, white.
- Headline: Inter 32, white, mixed weights: "Tudo sobre" light (300); "filmes", "séries", "animes",
  "muito mais." bold (700); commas and "e" medium (500). In English the nouns stay bold.
- Body: Inter 400 15, white, line height 24.
- Primary button: full content width (315) × 43, corner radius 60, accent purple gradient, 12 pt padding;
  label Inter 600 16 white, centered. Used again on show.

## Copy
| Key | pt-BR | en _(proposed)_ |
|---|---|---|
| Headline | "Tudo sobre filmes, séries, animes e muito mais." | "All about movies, series, anime and much more." |
| Body | "Fique por dentro das informações de filmes, séries, animes e muito mais." | "Stay up to date on movies, series, anime and much more." |
| Button | "Acessar" | "Get started" |

## Assets
- img_onboarding_bg, img_logo_tmdb

## Notes
- No designer notes in the frame.
