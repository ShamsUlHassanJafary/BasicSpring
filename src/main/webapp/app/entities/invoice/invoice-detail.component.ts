import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faFilePdf, faPaperPlane, faSpinner } from '@fortawesome/free-solid-svg-icons';

import { IInvoice } from 'app/shared/model/invoice.model';
import { JhiAlertService } from 'ng-jhipster';
import { BookingService } from '../booking/booking.service';

@Component({
  selector: 'jhi-invoice-detail',
  templateUrl: './invoice-detail.component.html',
})
export class InvoiceDetailComponent implements OnInit {
  invoice: IInvoice | null = null;

  isDownloading = false;

  downloadingMessage = 'Download Invoice';

  sendingMessage = 'Send Invoice';

  isSending = false;

  faSendStatus = faPaperPlane;

  faDownloadStatus = faFilePdf;

  constructor(
    protected bookingService: BookingService,
    protected alertService: JhiAlertService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ invoice }) => (this.invoice = invoice));
  }

  generateAndDownloadInvoice(): void {
    this.faDownloadStatus = faSpinner;
    this.isDownloading = true;
    if (this.invoice?.booking?.id) {
      this.downloadingMessage = 'Generating Invoice';
      this.bookingService.generateAndDownloadInvoice(this.invoice?.booking.id).subscribe((res: any) => {
        this.downloadingMessage = 'Downloading Invoice';
        const headers = res.headers;
        const contentDisposition = headers.get('content-disposition');
        const result = contentDisposition.split(';')[2]?.trim().split('=')[1];
        const anchor = document.createElement('a');
        anchor.download = result?.replace(/"/g, '');
        anchor.href = (window.webkitURL || window.URL).createObjectURL(res.body);
        anchor.dataset.downloadurl = ['application/pdf', anchor.download, anchor.href].join(':');
        anchor.click();
        this.alertService.success('Invoice downloaded.');
        this.resetDownloadingInvoiceState();
        (window.webkitURL || window.URL).revokeObjectURL(res.body);
      });
    }
  }

  sendInvoice(): void {
    this.faSendStatus = faSpinner;
    this.isSending = true;
    if (this.invoice?.booking) {
      this.sendingMessage = 'Sending Invoice';
      this.bookingService.sendInvoice(this.invoice?.booking).subscribe(() => {
        this.resetSendingInvoiceState();
      });
    }
  }

  resetSendingInvoiceState(): void {
    this.isSending = false;
    this.faSendStatus = faPaperPlane;
    this.sendingMessage = 'Send Invoice';
  }

  resetDownloadingInvoiceState(): void {
    this.isDownloading = false;
    this.faDownloadStatus = faPaperPlane;
    this.downloadingMessage = 'Download Invoice';
  }

  previousState(): void {
    window.history.back();
  }
}
