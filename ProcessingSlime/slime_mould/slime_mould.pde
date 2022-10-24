float offsetLength = 50;
PImage map;

ArrayList<Cell> c = new ArrayList<Cell>();

void setup() {
  size(600, 600);
  map = loadImage("resource/testmap.png");
  map.filter(INVERT);

  int[] mask = new int[map.pixels.length];
  for (int i = 0; i < mask.length; i++) {
    mask[i] = 1;
  }
  map.mask(mask);
  
  noStroke();
  background(0);
  
  for (int i = 0; i < 2000; i++) {
    c.add(new Cell(random(width), random(height)));
  }
  
  noStroke();
}



void draw() {
  fill(0, 3);
  
  rect(0, 0, width, height);
  image(map, 0, 0);
  loadPixels();
  
  //for(int j = 0; j < 2; j++)
  for (int i = 0; i < c.size(); i++) {
    c.get(i).Update();
  }

  drawCells(c);

  if (mousePressed) {
    push();
    fill(255);
    circle(mouseX, mouseY, 50);
    pop();
  }
  
  filter(BLUR, 1);
}

void drawCells(ArrayList<Cell> cells) {
  fill(255);
  for (int i = 0; i < cells.size(); i++) {

    rect(c.get(i).pos.x, c.get(i).pos.y, 2, 2);
  }
}

PVector rotate2D(PVector v, float theta) {
  return new PVector(
    v.x * cos(theta) - v.y * sin(theta), 
    v.x * sin(theta) + v.y * cos(theta)
    );
}

float WrapValue(float n, float mod) {
  return (n + mod) % mod;
}
