import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faPaperPlane, faFilePdf, faSpinner } from '@fortawesome/free-solid-svg-icons';

import { IQuote } from 'app/shared/model/quote.model';
import { JhiAlertService } from 'ng-jhipster';
import { BookingService } from '../booking/booking.service';

@Component({
  selector: 'jhi-quote-detail',
  templateUrl: './quote-detail.component.html',
})
export class QuoteDetailComponent implements OnInit {
  quote: IQuote | null = null;

  isDownloading = false;

  downloadingMessage = 'Download Quote';

  sendingMessage = 'Send Quote';

  isSending = false;

  faSendStatus = faPaperPlane;

  faDownloadStatus = faFilePdf;

  constructor(
    protected bookingService: BookingService,
    protected alertService: JhiAlertService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quote }) => (this.quote = quote));
  }

  generateAndDownloadQuote(): void {
    this.faDownloadStatus = faSpinner;
    this.isDownloading = true;
    if (this.quote?.booking?.id) {
      this.downloadingMessage = 'Generating Quote';
      this.bookingService.generateAndDownloadInvoice(this.quote?.booking.id).subscribe((res: any) => {
        this.downloadingMessage = 'Downloading Quote';
        const headers = res.headers;
        const contentDisposition = headers.get('content-disposition');
        const result = contentDisposition.split(';')[2]?.trim().split('=')[1];
        const anchor = document.createElement('a');
        anchor.download = result?.replace(/"/g, '');
        anchor.href = (window.webkitURL || window.URL).createObjectURL(res.body);
        anchor.dataset.downloadurl = ['application/pdf', anchor.download, anchor.href].join(':');
        anchor.click();
        this.alertService.success('Quote downloaded.');
        this.resetDownloadingState();
        (window.webkitURL || window.URL).revokeObjectURL(res.body);
      });
    }
  }

  sendQuote(): void {
    this.faSendStatus = faSpinner;
    this.isSending = true;
    if (this.quote?.booking) {
      this.sendingMessage = 'Sending Quote';
      this.bookingService.sendInvoice(this.quote?.booking).subscribe(() => {
        this.resetSendingState();
      });
    }
  }

  resetSendingState(): void {
    this.isSending = false;
    this.faSendStatus = faPaperPlane;
    this.sendingMessage = 'Send Quote';
  }

  resetDownloadingState(): void {
    this.isDownloading = false;
    this.faDownloadStatus = faPaperPlane;
    this.downloadingMessage = 'Download Quote';
  }

  previousState(): void {
    window.history.back();
  }
}
