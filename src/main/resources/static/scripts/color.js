const tileColors = {
  0:   "#cdc1b4",
  2:   "#eee4da",
  4:   "#ede0c8",
  8:   "#edcf72",
  16:  "#edcc61",
  32:  "#edc850",
  64:  "#edc53f",
  128: "#edc22e",
  256: "#f2b179",
  512: "#f59563",
  1024:"#f67c5f",
  2048:"#f65e3b",
  4096:"#3c3a32",
  8192:"#2f2d27",
};

export function getBgColor(value) {
    return tileColors[value]
}