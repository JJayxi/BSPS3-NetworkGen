class Cell {
  PVector pos, dir;
  float angle, size;

  Cell(float x, float y) {
    this.pos = new PVector(x, y);
    this.dir = PVector.random2D().setMag(1);
    this.angle = PI / 4;
    this.size = 1;
  }

  void Update() {
    Smell();
    Move();
  }

  boolean Smell() {
    int F, FL, FR;

    PVector offsetDirection = this.dir.copy().setMag(offsetLength);
    PVector leftVector = rotate2D(offsetDirection, -PI * .25);
    PVector rightVector = rotate2D(offsetDirection, PI * .25);  
    PVector Fdir = new PVector(WrapValue(this.pos.x + offsetDirection.x, width), WrapValue(this.pos.y + offsetDirection.y, height));
    PVector FLdir = new PVector(WrapValue(this.pos.x + leftVector.x, width), WrapValue(this.pos.y + leftVector.y, height));
    PVector FRdir = new PVector(WrapValue(this.pos.x + rightVector.x, width), WrapValue(this.pos.y + rightVector.y, height));

    int i = (floor(Fdir.y) * width + floor(Fdir.x));
    F = pixels[i];

    i = (floor(FLdir.y) * width + floor(FLdir.x));
    FL = pixels[i];

    i = (floor(FRdir.y) * width + floor(FRdir.x));
    FR = pixels[i];

    if (F > FL && F > FR) {
      return true;
    } else if (F < FL && F < FR) {
      if (random(1) < .5) {
        this.TurnLeft();
        return true;
      } else {
        this.TurnRight();
        return true;
      }
    } else if (FL < FR) {
      this.TurnRight();
      return true;
    } else if (FR < FL) {
      this.TurnLeft();
      return true;
    } else {
      return false;
    }
  }
  void TurnLeft() {
    this.dir.rotate(-this.angle);
  }

  void TurnRight() {
    this.dir.rotate(this.angle);
  }

  void Move() {
    this.pos.x = (this.pos.x + this.dir.x + width) % width;
    this.pos.y = (this.pos.y + this.dir.y + height) % height;
  }
}
