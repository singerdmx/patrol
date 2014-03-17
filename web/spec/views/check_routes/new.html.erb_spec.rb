require 'spec_helper'

describe "check_routes/new" do
  before(:each) do
    assign(:check_route, stub_model(CheckRoute).as_new_record)
  end

  it "renders new check_route form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", check_routes_path, "post" do
    end
  end
end
