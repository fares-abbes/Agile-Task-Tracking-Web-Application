import { Component, ElementRef, HostBinding, Input, AfterViewInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-scroll-area',
  templateUrl: './scroll-area.component.html',
  styleUrls: ['./scroll-area.component.css']
})
export class ScrollAreaComponent implements AfterViewInit {
  @Input() class = '';
  @ViewChild('scroller') scroller!: ElementRef<HTMLElement>;

  atTop = true;
  atBottom = false;

  ngAfterViewInit(): void {
    const el = this.scroller.nativeElement;
    this.updateShadows(el);
    el.addEventListener('scroll', () => this.updateShadows(el));
    // react to resize/content changes
    new ResizeObserver(() => this.updateShadows(el)).observe(el);
  }

  private updateShadows(el: HTMLElement) {
    this.atTop = el.scrollTop === 0;
    this.atBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 1;
  }
}
