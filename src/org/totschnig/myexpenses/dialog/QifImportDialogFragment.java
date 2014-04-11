package org.totschnig.myexpenses.dialog;

import static org.totschnig.myexpenses.provider.DatabaseConstants.KEY_LABEL;
import static org.totschnig.myexpenses.provider.DatabaseConstants.KEY_ROWID;

import org.totschnig.myexpenses.R;
import org.totschnig.myexpenses.activity.QifImport;
import org.totschnig.myexpenses.export.qif.QifDateFormat;
import org.totschnig.myexpenses.provider.TransactionProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class QifImportDialogFragment extends ImportSourceDialogFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
  Spinner mAccountSpinner;
  Spinner mDateFormatSpinner;
  private SimpleCursorAdapter mAccountsAdapter;
  public static final QifImportDialogFragment newInstance() {
    return new QifImportDialogFragment();
  }
  protected int getLayoutId() {
    return R.layout.qif_import_dialog;
  }
  protected int getLayoutTitle() {
    return R.string.pref_import_qif_title;
  }
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Context wrappedCtx = DialogUtils.wrapContext2(getActivity());
    LayoutInflater li = LayoutInflater.from(wrappedCtx);
    View view = li.inflate(R.layout.qif_import_dialog, null);
    mFilename = (EditText) view.findViewById(R.id.Filename);
    mFilename.addTextChangedListener(new TextWatcher(){
      public void afterTextChanged(Editable s) {
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
            !TextUtils.isEmpty(s.toString()));
      }
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}
      public void onTextChanged(CharSequence s, int start, int before, int count){}
    });
    mAccountSpinner = (Spinner) view.findViewById(R.id.Account);
    mDateFormatSpinner = (Spinner) view.findViewById(R.id.DateFormat);
    ArrayAdapter<QifDateFormat> dateFormatAdapter =
        new ArrayAdapter<QifDateFormat>(
            getActivity(), android.R.layout.simple_spinner_item, QifDateFormat.values());
    dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mDateFormatSpinner.setAdapter(dateFormatAdapter);
    mAccountsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null,
        new String[] {KEY_LABEL}, new int[] {android.R.id.text1}, 0);
    mAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mAccountSpinner.setAdapter(mAccountsAdapter);
    view.findViewById(R.id.btn_browse).setOnClickListener(this);
    getLoaderManager().initLoader(0, null, this);
    mDialog = new AlertDialog.Builder(wrappedCtx)
      .setTitle(R.string.pref_import_qif_title)
      .setView(view)
      .setPositiveButton(android.R.string.ok,this)
      .setNegativeButton(android.R.string.cancel,this)
      .create();
    return mDialog;
  }

  @Override
  public void onClick(DialogInterface dialog, int id) {
    if (id == AlertDialog.BUTTON_POSITIVE) {
      ((QifImport) getActivity()).onSourceSelected(
          mFilename.getText().toString(),
          (QifDateFormat) mDateFormatSpinner.getSelectedItem(),
          mAccountSpinner.getSelectedItemId()
          );
    } else {
      super.onClick(dialog, id);
    }
  }
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    CursorLoader cursorLoader = new CursorLoader(
        getActivity(),
        TransactionProvider.ACCOUNTS_BASE_URI,
        new String[] {KEY_ROWID,KEY_LABEL},
        null,null, null);
    return cursorLoader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    MatrixCursor extras = new MatrixCursor(new String[] { KEY_ROWID,KEY_LABEL });
    extras.addRow(new String[] { "0", getString(R.string.menu_create_account)});
    mAccountsAdapter.swapCursor(new MergeCursor(new Cursor[] {extras,data}));
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mAccountsAdapter.swapCursor(null);
  }

  @Override
  protected void setupDialogView(View view) {
    super.setupDialogView(view);
    mAccountSpinner = (Spinner) view.findViewById(R.id.Account);
    mDateFormatSpinner = (Spinner) view.findViewById(R.id.DateFormat);
    ArrayAdapter<QifDateFormat> dateFormatAdapter =
        new ArrayAdapter<QifDateFormat>(
            getActivity(), android.R.layout.simple_spinner_item, QifDateFormat.values());
    dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mDateFormatSpinner.setAdapter(dateFormatAdapter);
    mAccountsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item, null,
        new String[] {KEY_LABEL}, new int[] {android.R.id.text1}, 0);
    mAccountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mAccountSpinner.setAdapter(mAccountsAdapter);
    getLoaderManager().initLoader(0, null, this);
  }
}